package io.terminus.dalaran.component.trigger.transfer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.as2.api.AS2MediaType;

public class AS2TransferAfterProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, AS2MediaType.APPLICATION_EDIFACT);
    }
}
