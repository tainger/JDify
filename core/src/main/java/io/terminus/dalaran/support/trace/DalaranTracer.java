package io.terminus.dalaran.support.trace;

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

import static io.terminus.dalaran.DalaranConstants.*;

/**
 * 尝试过 InterceptStrategy 和 TraceEventHandler, 最后决定自己写前后 processor 处理
 * 主要 CamelInternalProcessorAdvice 没有可用入口, 否则直接使用也是比较合适的
 */
@Slf4j
public class DalaranTracer {

    private final DalaranTraceLogger logger;

    private final TracingType tracingType;

    private final Long triggerId;

    private final Long flowId;

    private final Long processorId;


    public DalaranTracer(DalaranTraceLogger logger, Long triggerId) {
        this(logger, TracingType.Trigger, triggerId, null, null);
    }

    public DalaranTracer(DalaranTraceLogger logger, Long triggerId, Long flowId, Long processorId) {
        this(logger, TracingType.Flow, triggerId, flowId, processorId);
    }

    private DalaranTracer(DalaranTraceLogger logger, TracingType tracingType, Long triggerId, Long flowId, Long processorId) {
        this.logger = logger;
        this.tracingType = tracingType;
        this.triggerId = triggerId;
        this.flowId = flowId;
        this.processorId = processorId;
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
            switch (tracingType) {
                case Flow:
                    exchange.setProperty(FLOW_TRACING_LOG, tracingLog);
                    break;
                case Trigger:
                    exchange.setProperty(TRIGGER_TRACING_LOG, tracingLog);
                    break;
            }

            tracingLog.setRecordId(exchange.getExchangeId());
            tracingLog.setTracingType(tracingType);
            tracingLog.setTriggerId(triggerId);
            tracingLog.setFlowId(flowId);
            tracingLog.setProcessorId(processorId);
            tracingLog.setTimestamp(System.currentTimeMillis());
            tracingLog.setInputBody(MessageHelper.extractBodyForLogging(exchange.getIn(), ""));
            tracingLog.setInputBodyType(bodyModelType);

        }

        @Override
        public String getTraceLabel() {
            switch (tracingType) {
                case Flow:
                    return "flow tracing before[flow(" + flowId + ") -> processor(" + processorId + ")]";
                case Trigger:
                    return "trigger tracing before[trigger(" + triggerId + ")]";
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

            DalaranTracingLog tracingLog = null;
            switch (tracingType) {
                case Flow:
                    tracingLog = exchange.getProperty(FLOW_TRACING_LOG, DalaranTracingLog.class);
                    break;
                case Trigger:
                    tracingLog = exchange.getProperty(TRIGGER_TRACING_LOG, DalaranTracingLog.class);
                    break;
            }
            if (tracingLog == null) {
                return;
            }
            tracingLog.setOutputBody(MessageHelper.extractBodyForLogging(exchange.getIn(), ""));
            tracingLog.setOutputBodyType(bodyModelType);
            tracingLog.setElapsed(System.currentTimeMillis() - tracingLog.getTimestamp());

            Boolean isTestFlow = exchange.getProperty(TEST_FLOW, Boolean.class);
            if (isTestFlow != null) {
                tracingLog.setTestFlow(isTestFlow);
            } else {
                tracingLog.setTestFlow(false);
            }
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
