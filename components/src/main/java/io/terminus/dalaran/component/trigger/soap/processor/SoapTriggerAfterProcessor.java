package io.terminus.dalaran.component.trigger.soap.processor;

import io.terminus.dalaran.component.trigger.soap.SoapListenerConfig;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class SoapTriggerAfterProcessor implements Processor {

    private SoapListenerConfig config;

    public SoapTriggerAfterProcessor(SoapListenerConfig config) {
        this.config = config;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/xml");
        if (config.isNullResponseBody()) {
            exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "text/xml");
            exchange.getOut().setBody(null);
        }
    }
}
