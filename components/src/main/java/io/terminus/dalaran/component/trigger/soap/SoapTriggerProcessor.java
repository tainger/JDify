package io.terminus.dalaran.component.trigger.soap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by jingdi on 2019/7/2
 */
public class SoapTriggerProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/xml");
    }
}
