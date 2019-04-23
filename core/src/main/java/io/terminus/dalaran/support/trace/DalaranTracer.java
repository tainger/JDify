package io.terminus.dalaran.support.trace;

import com.google.gson.Gson;
import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.DalaranTraceLogger;
import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.model.DalaranTracingLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.util.MessageHelper;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.*;

/**
 * 尝试过 InterceptStrategy 和 TraceEventHandler, 最后决定自己写前后 processor 处理
 * 主要 CamelInternalProcessorAdvice 没有可用入口, 否则直接使用也是比较合适的
 */
@Slf4j
public class DalaranTracer {

    private final Gson gson = new Gson();

    private final DalaranTraceLogger logger;

    private final TracingType tracingType;

    private final Long triggerId;

    private final Long flowId;

    private final Long processorId;

    private DalaranTracer(DalaranTraceLogger logger, TracingType tracingType, Long triggerId, Long flowId, Long processorId) {
        this.logger = logger;
        this.tracingType = tracingType;
        this.triggerId = triggerId;
        this.flowId = flowId;
        this.processorId = processorId;
    }


    public static DalaranTracer buildTriggerTracer(DalaranTraceLogger logger, Long triggerId) {
        return new DalaranTracer(logger, TracingType.Trigger, triggerId, null, null);
    }

    public static DalaranTracer buildTestFlowTracer(DalaranTraceLogger logger, Long flowId) {
        return new DalaranTracer(logger, TracingType.TestFlow, null, flowId, null);
    }

    public static DalaranTracer buildFlowTracer(DalaranTraceLogger logger, Long triggerId, Long flowId, Long processorId) {
        return new DalaranTracer(logger, TracingType.Flow, triggerId, flowId, processorId);
    }

    public static DalaranTracer buildConvertTracer(DalaranTraceLogger logger, Long triggerId, Long flowId, Long processorId) {
        return new DalaranTracer(logger, TracingType.Convert, triggerId, flowId, processorId);
    }

    public void before(RouteDefinition route, BodyModelType modelType) {
        route.process(this.buildBeforeProcessor(modelType));
    }

    public void after(RouteDefinition route, BodyModelType modelType) {
        route.process(this.buildAfterProcessor(modelType));
    }

    // TODO async
    private Processor buildBeforeProcessor(BodyModelType bodyModelType) {
        return new TraceBeforeProcessor(bodyModelType);
    }

    // TODO async
    private Processor buildAfterProcessor(BodyModelType bodyModelType) {
        return new TraceAfterProcessor(bodyModelType);
    }

    private String getTracingLogPropertyKey() {
        switch (tracingType) {
            case Flow:
                return FLOW_TRACING_LOG;
            case Trigger:
                return TRIGGER_TRACING_LOG;
            case TestFlow:
                return TEST_FLOW_TRACING_LOG;
            case Convert:
                return CONVERT_TRACING_LOG;
            default:
                throw new RuntimeException("not support tracing type[" + tracingType + "]");
        }
    }

    private String extractBody(Object body) {
        if (body == null) {
            return null;
        }
        if (body instanceof String) {
            return (String) body;
        }
        if (body instanceof Map || body instanceof Iterable || body instanceof Serializable) {
            return gson.toJson(body);
        }
        return body.toString();
    }

    private class TraceBeforeProcessor implements Processor, Traceable {

        private final BodyModelType bodyModelType;

        private TraceBeforeProcessor(BodyModelType bodyModelType) {
            this.bodyModelType = bodyModelType;
        }

        @Override
        public void process(Exchange exchange) {
            // 保持输入输出不变
            exchange.getOut().copyFrom(exchange.getIn());

            DalaranTracingLog tracingLog = new DalaranTracingLog();

            if (TracingType.Flow == tracingType || TracingType.Convert == tracingType) {
                tracingLog.setMain(false);
            } else {
                tracingLog.setMain(true);
            }
            exchange.setProperty(getTracingLogPropertyKey(), tracingLog);
            String testRecordId = exchange.getProperty(TEST_FLOW_RECORD_ID_HEADER, String.class);
            if (testRecordId != null) {
                tracingLog.setRecordId(testRecordId);
            } else {
                tracingLog.setRecordId(exchange.getExchangeId());
            }
            tracingLog.setTracingType(tracingType);
            tracingLog.setTriggerId(triggerId);
            tracingLog.setFlowId(flowId);
            tracingLog.setProcessorId(processorId);
            tracingLog.setTimestamp(System.currentTimeMillis());
            tracingLog.setInputBody(extractBody(exchange.getIn().getBody()));
            tracingLog.setInputBodyType(bodyModelType);
        }

        @Override
        public String getTraceLabel() {
            switch (tracingType) {
                case Flow:
                    return "flow tracing before[flow(" + flowId + ") -> processor(" + processorId + ")]";
                case Trigger:
                    return "trigger tracing before[trigger(" + triggerId + ")]";
                case TestFlow:
                    return "test flow tracing before[testFlow(" + flowId + ")]";
                default:
                    return "DalaranTraceBefore";
            }
        }

        @Override
        public String toString() {
            return getTraceLabel();
        }
    }

    private class TraceAfterProcessor implements Processor, Traceable {

        private final BodyModelType bodyModelType;

        private TraceAfterProcessor(BodyModelType bodyModelType) {
            this.bodyModelType = bodyModelType;
        }

        @Override
        public void process(Exchange exchange) {
            // 保持输入输出不变
            exchange.getOut().copyFrom(exchange.getIn());
            String propertyKey = getTracingLogPropertyKey();
            DalaranTracingLog tracingLog = exchange.getProperty(propertyKey, DalaranTracingLog.class);
            if (tracingLog == null) {
                return;
            }
            exchange.removeProperty(propertyKey);
            tracingLog.setSuccessful(true);
            tracingLog.setOutputBody(extractBody(exchange.getIn().getBody()));
            tracingLog.setOutputBodyType(bodyModelType);
            tracingLog.setElapsed(System.currentTimeMillis() - tracingLog.getTimestamp());
//            tracingLog.setOutputHeaders(exchange.getIn().getHeaders());

            logger.log(tracingLog);
        }

        @Override
        public String getTraceLabel() {
            switch (tracingType) {
                case Flow:
                    return "flow tracing after[flow(" + flowId + ") -> processor(" + processorId + ")]";
                case Trigger:
                    return "trigger tracing after[trigger(" + triggerId + ")]";
                case TestFlow:
                    return "test flow tracing after[testFlow(" + flowId + ")]";
                default:
                    return "DalaranTraceAfter";
            }
        }

        @Override
        public String toString() {
            return getTraceLabel();
        }
    }
}
