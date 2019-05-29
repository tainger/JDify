package io.terminus.dalaran.core.log;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.core.DalaranConstants;
import io.terminus.dalaran.core.model.BodyType;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.camel.model.RouteDefinition;

import java.io.Serializable;
import java.util.Map;

/**
 * 尝试过 InterceptStrategy 和 TraceEventHandler, 最后决定自己写前后 processor 处理
 * 主要 CamelInternalProcessorAdvice 没有可用入口, 否则直接使用也是比较合适的
 */
@Slf4j
public class DalaranTracer {

    private final DalaranTraceLogger logger;

    private final TracingType tracingType;

    private final Long flowId;

    private final String processorId;

    private DalaranTracer(DalaranTraceLogger logger, TracingType tracingType, Long flowId, String processorId) {
        this.logger = logger;
        this.tracingType = tracingType;
        this.flowId = flowId;
        this.processorId = processorId;
    }

    public static DalaranTracer buildTestFlowTracer(DalaranTraceLogger logger, Long flowId) {
        return new DalaranTracer(logger, TracingType.TestFlow, flowId, null);
    }

    public static DalaranTracer buildFlowTracer(DalaranTraceLogger logger, Long flowId) {
        return new DalaranTracer(logger, TracingType.Flow, flowId, null);
    }

    public static DalaranTracer buildFlowSpanTracer(DalaranTraceLogger logger, Long flowId, String processorId) {
        return new DalaranTracer(logger, TracingType.Processor, flowId, processorId);
    }

    public static DalaranTracer buildConvertTracer(DalaranTraceLogger logger, Long flowId, String processorId) {
        return new DalaranTracer(logger, TracingType.Convert, flowId, processorId);
    }

    public static DalaranTracer buildSubFlowTracer(DalaranTraceLogger logger, Long flowId) {
        return new DalaranTracer(logger, TracingType.SubFlow, flowId, null);
    }

    public void before(RouteDefinition route, BodyType modelType) {
        route.process(this.buildBeforeProcessor(modelType));
    }

    public void after(RouteDefinition route, BodyType modelType) {
        route.process(this.buildAfterProcessor(modelType));
    }

    // TODO async
    private Processor buildBeforeProcessor(BodyType bodyModelType) {
        return new TraceBeforeProcessor(bodyModelType);
    }

    // TODO async
    private Processor buildAfterProcessor(BodyType bodyModelType) {
        return new TraceAfterProcessor(bodyModelType);
    }

    private String getTracingLogPropertyKey() {
        switch (tracingType) {
            case Flow:
                return DalaranConstants.FLOW_TRACING_LOG;
            case TestFlow:
                return DalaranConstants.TEST_FLOW_TRACING_LOG;
            case Processor:
                return DalaranConstants.PROCESSOR_TRACING_LOG;
            case Convert:
                return DalaranConstants.CONVERT_TRACING_LOG;
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
        if (body instanceof byte[]) {
            return new String((byte[]) body);
        }
        if (body instanceof Map || body instanceof Iterable || body instanceof Serializable) {
            return JSON.toJSONString(body);
        }
        return body.toString();
    }

    private class TraceBeforeProcessor implements Processor, Traceable {

        private final BodyType bodyModelType;

        private TraceBeforeProcessor(BodyType bodyModelType) {
            this.bodyModelType = bodyModelType;
        }

        @Override
        public void process(Exchange exchange) {
            // 保持输入输出不变
            exchange.getOut().copyFrom(exchange.getIn());

            DalaranTracingLog tracingLog = new DalaranTracingLog();
            Boolean isMainFlow = TracingType.Flow == tracingType || TracingType.TestFlow == tracingType;
            tracingLog.setMain(isMainFlow);
            exchange.setProperty(getTracingLogPropertyKey(), tracingLog);
            String testRecordId = exchange.getProperty(DalaranConstants.TEST_FLOW_RECORD_ID_HEADER, String.class);
            if (testRecordId != null) {
                tracingLog.setRecordId(testRecordId);
            } else {
                tracingLog.setRecordId(exchange.getExchangeId());
            }
            tracingLog.setTracingType(tracingType);
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

        private final BodyType bodyModelType;

        private TraceAfterProcessor(BodyType bodyModelType) {
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
