package io.terminus.dalaran.camel.component.dubbo;

import com.alibaba.dubbo.common.utils.StringUtils;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

public class DubboCamelProcessor extends DefaultProducer {

    private final DubboEndpoint endpoint;
    private final String method;
    private final String parameterType;

    public DubboCamelProcessor(DubboEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        this.method = endpoint.getMethod();
        this.parameterType = endpoint.getParameterType();
    }

    @Override
    public void process(Exchange exchange) {
        Object arg = exchange.getIn().getBody();
        Object result;
        if (StringUtils.isNotEmpty(parameterType)) {
            result = endpoint.getGenericService().$invoke(method, new String[]{parameterType}, new Object[]{arg});
        } else {
            result = endpoint.getGenericService().$invoke(method, new String[]{}, new Object[]{});
        }
        exchange.getOut().setBody(result);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
