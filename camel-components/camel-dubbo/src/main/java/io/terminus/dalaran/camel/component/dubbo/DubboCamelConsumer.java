package io.terminus.dalaran.camel.component.dubbo;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

public class DubboCamelConsumer extends DefaultConsumer {

    private final DubboEndpoint endpoint;

    private final DubboGenericProvider genericProvider;

    public DubboCamelConsumer(DubboEndpoint endpoint, Processor processor, DubboGenericProvider genericProvider) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.genericProvider = genericProvider;
    }

    @Override
    public void doStart() throws Exception {
        super.doStart();
        genericProvider.registerMethod(endpoint.getMethod(), endpoint, getProcessor());
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        genericProvider.unregisterMethod(endpoint.getMethod());
    }
}
