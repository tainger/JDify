package io.terminus.dalaran.support.trace;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.model.DalaranTracingInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Processor;
import org.apache.camel.util.ExchangeHelper;
import org.apache.camel.util.MessageHelper;

@Slf4j
public class DalaranTracer {

    private final DalaranTracingInfo tracingInfo;

    public DalaranTracer() {
        this.tracingInfo = new DalaranTracingInfo();
    }

    public DalaranTracer(Long processorId) {
        this();
        tracingInfo.setProcessorId(processorId);
    }

    public Processor buildBeforeProcessor(BodyModelType bodyModelType) {
        return exchange -> {
            tracingInfo.setTriggerId(exchange.getProperty("trigger_id", Long.class));
            tracingInfo.setFlowId(exchange.getProperty("flow_id", Long.class));
            tracingInfo.setTimestamp(System.currentTimeMillis());
            tracingInfo.setInputBody(MessageHelper.extractBodyForLogging(exchange.getIn(), ""));
            tracingInfo.setInputBodyType(bodyModelType);
            tracingInfo.setInputHeaders(exchange.getIn().getHeaders());

            // 保持输入输出不变
            exchange.setOut(exchange.getIn());
        };
    }

    public Processor buildAfterProcessor(BodyModelType bodyModelType) {
        return exchange -> {
            tracingInfo.setOutputBody(MessageHelper.extractBodyForLogging(exchange.getIn(), ""));
            tracingInfo.setOutputBodyType(bodyModelType);
            tracingInfo.setOutputHeaders(exchange.getIn().getHeaders());
            tracingInfo.setElapsed(System.currentTimeMillis() - tracingInfo.getTimestamp());

            // 保持输入输出不变
            exchange.setOut(exchange.getIn());
            log.info("tracing: " + tracingInfo.toString());
        };
    }
}
