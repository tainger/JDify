package io.terminus.dalaran.camel.component.dubbo;

import com.alibaba.dubbo.common.utils.StringUtils;
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

    private ServiceConfig providerConfig;
    private final DubboEndpoint endpoint;

    public DubboGenericProvider(DubboEndpoint endpoint) {
        this.endpoint = endpoint;
        buildProviderConfig();
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
            if (exchange.getException() != null) {
                throw new RuntimeException(exchange.getException().getMessage(), exchange.getException().getCause());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        return exchange.getOut().getBody();
    }

    public void registerMethod(String method, DubboEndpoint endpoint, Processor processor) throws InterruptedException {
        endpointMap.put(method, endpoint);
        processorMap.put(method, processor);

        if (!providerConfig.isExported()) {
            Thread.sleep(10000);
            providerConfig.export();
        }
    }

    public void unregisterMethod(String method) {
        endpointMap.remove(method);
        processorMap.remove(method);
        if (processorMap.isEmpty() && endpointMap.isEmpty() && providerConfig.isExported()) {
            providerConfig.unexport();
            // 当所有方法都不存在时, provider 会 unexport, 但是 Dubbo 里面有 unexport 和 exported 两个标志位, 所以这里需要充值一下, 以免 provider 丢失
            buildProviderConfig();
        }
    }

    private void buildProviderConfig() {
        this.providerConfig = new ServiceConfig();
        providerConfig.setRegistry(new RegistryConfig(endpoint.getRegistryAddress()));
        providerConfig.setTimeout(endpoint.getTimeout());
        providerConfig.setApplication(endpoint.getApplicationConfig());
        providerConfig.setVersion(endpoint.getVersion());
        providerConfig.setInterface(endpoint.getServiceId());
        providerConfig.setRetries(endpoint.getRetries());
        if (!StringUtils.isBlank(System.getenv("DICE_PROJECT_ID")) && !StringUtils.isBlank(System.getenv("DICE_WORKSPACE"))) {
            providerConfig.setOwner(System.getenv("DICE_PROJECT_ID") + "_" + System.getenv("DICE_WORKSPACE"));
        }
        providerConfig.setRef(this);
    }
}
