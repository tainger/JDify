package io.terminus.dalaran.camel.component.dubbo;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.HashMap;
import java.util.Map;

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
            try {
                result = endpoint.getGenericService().$invoke(method, new String[]{parameterType}, new Object[]{arg});
            } catch (RpcException e) {
                e.printStackTrace();
                GenericService genericService = refreshService();
                endpoint.setGenericService(genericService);
                result = genericService.$invoke(method, new String[]{parameterType}, new Object[]{arg});
            }
        } else {
            try {
                result = endpoint.getGenericService().$invoke(method, new String[]{}, new Object[]{});
            } catch (RpcException e) {
                e.printStackTrace();
                GenericService genericService = refreshService();
                endpoint.setGenericService(genericService);
                result = genericService.$invoke(method, new String[]{}, new Object[]{});
            }
        }
        exchange.getOut().setBody(result);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }

    private GenericService refreshService() {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setApplication(endpoint.getApplicationConfig());
        reference.setRegistry(new RegistryConfig(endpoint.getRegistryAddress()));
        reference.setVersion(endpoint.getVersion());
        reference.setTimeout(endpoint.getTimeout());
        reference.setInterface(endpoint.getServiceId());
        reference.setCheck(false);
        reference.setGeneric(true);
        reference.setRetries(endpoint.getRetries());
        Map<String, String> params = new HashMap<>();
        params.put("send.reconnect", "true");
        reference.setParameters(params);
        if (endpoint.getVersion().length() > 6) {
            reference.setOwner(endpoint.getVersion().substring(6));
        }
        return reference.get();
    }
}
