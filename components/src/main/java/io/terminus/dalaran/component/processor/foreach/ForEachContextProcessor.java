package io.terminus.dalaran.component.processor.foreach;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.flow.DalaranRoute;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForEachContextProcessor implements Processor {

    private static Logger logger = LoggerFactory.getLogger(ForEachContextProcessor.class);

    private DalaranRoute route;

    public ForEachContextProcessor(DalaranRoute route) {
        this.route = route;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("headers: " + exchange.getIn().getHeaders().toString());
        logger.info("body: " + exchange.getIn().getBody().toString());
        route.setProperty(DalaranConstants.DALARAN_CONTEXT_EXCHANGE, Builder.constant(exchange.getProperty(DalaranConstants.DALARAN_CONTEXT_EXCHANGE)));
    }
}
