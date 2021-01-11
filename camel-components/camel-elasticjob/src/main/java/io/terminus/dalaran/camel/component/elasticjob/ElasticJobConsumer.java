package io.terminus.dalaran.camel.component.elasticjob;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

public class ElasticJobConsumer extends DefaultConsumer {

    public ElasticJobConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    public ElasticJobEndpoint getEndpoint() {
        return (ElasticJobEndpoint) super.getEndpoint();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        getEndpoint().onConsumerStart(this);
    }

    @Override
    protected void doStop() throws Exception {
        getEndpoint().onConsumerStop(this);
        super.doStop();
    }
}
