package io.terminus.dalaran.camel.component.dubbo;

import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.rpc.service.GenericException;
import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;

import java.util.HashMap;
import java.util.Map;

public class DubboGenericProvider implements GenericService {

    private final Map<String, Processor> processorMap = new HashMap<>();
    private final Map<String, Endpoint> endpointMap = new HashMap<>();

    private final ServiceConfig providerConfig;

    public DubboGenericProvider(DubboEndpoint endpoint) {
        this.providerConfig = new ServiceConfig();
        providerConfig.setRegistry(new RegistryConfig(endpoint.getRegistryAddress()));
        providerConfig.setTimeout(endpoint.getTimeout());
        providerConfig.setApplication(endpoint.getApplicationConfig());
        providerConfig.setVersion(endpoint.getVersion());
        providerConfig.setInterface(endpoint.getServiceId());
        providerConfig.setRef(this);
    }

    @Override
    public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
        Processor processor = processorMap.get(method);
        Endpoint endpoint = endpointMap.get(method);
        if (processor == null || endpoint == null) {
            // TODO throw...
            return null;
        }
        Exchange exchange = endpoint.createExchange();
        Message message = new DefaultMessage(endpoint.getCamelContext());
        exchange.setMessage(message);
        if (args.length == 1) {
            message.setBody(args[0]);
        } else {
            message.setBody(args);
        }
        try {
            processor.process(exchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exchange.getOut().getBody();
    }

    public void registerMethod(String method, DubboEndpoint endpoint, Processor processor) {
        endpointMap.put(method, endpoint);
        processorMap.put(method, processor);
        if (!providerConfig.isExported()) {
            providerConfig.export();
        }
    }

    public void unregisterMethod(String method) {
        endpointMap.remove(method);
        processorMap.remove(method);
        if (processorMap.isEmpty() && endpointMap.isEmpty() && providerConfig.isExported()) {
            providerConfig.unexport();
        }
    }
}
