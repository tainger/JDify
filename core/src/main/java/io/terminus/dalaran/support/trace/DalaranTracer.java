package io.terminus.dalaran.support.trace;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.DalaranTraceLogger;
import io.terminus.dalaran.model.DalaranTracingLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Processor;
import org.apache.camel.util.MessageHelper;

import static io.terminus.dalaran.DalaranConstants.*;

/**
 * 尝试过 InterceptStrategy 和 TraceEventHandler, 最后决定自己写前后 processor 处理
 * 主要 CamelInternalProcessorAdvice 没有可用入口, 否则直接使用也是比较合适的
 */
@Slf4j
public class DalaranTracer {

    private final DalaranTraceLogger logger;

    private final TracingType traceType;

    public DalaranTracer(DalaranTraceLogger logger, TracingType traceType) {
        this.logger = logger;
        this.traceType = traceType;
    }

    // TODO async
    public Processor buildBeforeProcessor(BodyModelType bodyModelType) {
        return exchange -> {
            // 保持输入输出不变
            exchange.setOut(exchange.getIn());

            DalaranTracingLog tracingLog = new DalaranTracingLog();
            switch (traceType) {
                case Flow:
                    exchange.setProperty(FLOW_TRACING_LOG, tracingLog);
                    tracingLog.setFlowId(exchange.getProperty(CURRENT_FLOW_ID, Long.class));
                    tracingLog.setProcessorId(exchange.getProperty(CURRENT_PROCESSOR_ID, Long.class));
                    break;
                case Trigger:
                    exchange.setProperty(TRIGGER_TRACING_LOG, tracingLog);
                    break;
            }

            tracingLog.setTriggerId(exchange.getProperty(CURRENT_TRIGGER_ID, Long.class));
            tracingLog.setTimestamp(System.currentTimeMillis());
            tracingLog.setInputBody(MessageHelper.extractBodyForLogging(exchange.getIn(), ""));
            tracingLog.setInputBodyType(bodyModelType);

//            tracingLog.setInputHeaders(exchange.getIn().getHeaders());

        };
    }

    // TODO async
    public Processor buildAfterProcessor(BodyModelType bodyModelType) {
        return exchange -> {
            // 保持输入输出不变
            exchange.setOut(exchange.getIn());

            DalaranTracingLog tracingLog = null;
            switch (traceType) {
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
//            tracingLog.setOutputHeaders(exchange.getIn().getHeaders());

            logger.log(tracingLog);
        };
    }
}
