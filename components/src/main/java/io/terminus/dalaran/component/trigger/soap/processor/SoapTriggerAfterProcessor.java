package io.terminus.dalaran.component.trigger.soap.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class SoapTriggerAfterProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/xml");
    }
}
