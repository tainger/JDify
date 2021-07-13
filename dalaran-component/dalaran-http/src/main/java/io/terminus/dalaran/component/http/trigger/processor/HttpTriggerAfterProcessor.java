package io.terminus.dalaran.component.http.trigger.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class HttpTriggerAfterProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
    }
}
