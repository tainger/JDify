package io.terminus.dalaran.camel.component.dubbo;

import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

public class DubboCamelProcessor extends DefaultProducer {

    private final GenericService genericService;

    private final String method;
    private final String parameterType;

    public DubboCamelProcessor(DubboEndpoint endpoint) {
        super(endpoint);
        this.genericService = endpoint.getGenericService();
        this.method = endpoint.getMethod();
        this.parameterType = endpoint.getParameterType();
    }

    @Override
    public void process(Exchange exchange) {
        Object arg = exchange.getIn().getBody();
        Object result = genericService.$invoke(method, new String[]{}, new Object[]{arg});
        exchange.getOut().setBody(result);
    }
}
