package io.terminus.dalaran.camel.component.dubbo;

import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

public class DubboCamelProcessor extends DefaultProducer {

    private final GenericService genericService;

    private final String method;
    private final String[] parameterTypes;

    public DubboCamelProcessor(DubboEndpoint endpoint) {
        super(endpoint);
        this.genericService = endpoint.getGenericService();
        this.method = endpoint.getMethod();
        this.parameterTypes = endpoint.getParameterTypes().toArray(new String[0]);
    }

    @Override
    public void process(Exchange exchange) {
        Object[] args = null;
        try {
            args = exchange.getIn().getBody(Object[].class);
        } catch (ClassCastException e) {
            // TODO no args
        }
        Object result = genericService.$invoke(method, parameterTypes, args);
        exchange.getOut().setBody(result);
    }
}
