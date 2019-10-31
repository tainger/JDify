package io.terminus.dalaran.component.processor.mq;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class DalaranMQMessageProcessor implements Processor {

    private final DalaranMQProducerConfig producerConfig;

    public DalaranMQMessageProcessor(DalaranMQProducerConfig producerConfig) {
        this.producerConfig = producerConfig;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (!producerConfig.isMessageSharding()) {
            return;
        }

    }
}
