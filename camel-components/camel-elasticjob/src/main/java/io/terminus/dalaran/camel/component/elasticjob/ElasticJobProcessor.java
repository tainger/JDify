package io.terminus.dalaran.camel.component.elasticjob;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

public class ElasticJobProcessor extends DefaultProducer {

    private final ElasticJobEndpoint endpoint;

    public ElasticJobProcessor(ElasticJobEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getOut().setBody(null);
    }
}
