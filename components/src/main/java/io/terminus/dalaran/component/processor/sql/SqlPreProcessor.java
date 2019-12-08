package io.terminus.dalaran.component.processor.sql;

import io.terminus.dalaran.ComponentConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class SqlPreProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(ComponentConstants.SQL_RETRIEVE_GENERATED_KEYS, true);
    }
}
