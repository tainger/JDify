package io.terminus.dalaran.camel.component.dubbo;

import com.alibaba.dubbo.common.utils.PojoUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultMessage;

public class DubboCamelConsumer extends DefaultConsumer {

    private final DubboEndpoint endpoint;

    private final ServiceConfig provider;

    public DubboCamelConsumer(DubboEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.provider = createProvider();
    }

    @Override
    public void doStart() throws Exception {
        super.doStart();
        provider.export();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        provider.unexport();
    }

    private ServiceConfig createProvider() {
        ServiceConfig provider = new ServiceConfig();
        provider.setApplication(endpoint.getApplicationConfig());
        provider.setRegistry(new RegistryConfig(endpoint.getRegistryAddress()));
        provider.setVersion(endpoint.getVersion());
        provider.setInterface(endpoint.getServiceId());
        try {
            provider.setRef(createProxyBean());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return provider;
    }

    private Object createProxyBean() throws ClassNotFoundException {
        Class parameterClass;
        // TODO 这里还有问题
        if (StringUtils.isNotEmpty(endpoint.getParameterType())) {
            parameterClass = Class.forName(endpoint.getParameterType());
        } else {
            parameterClass = null;
        }
        return (GenericService) (method, parameterTypes, args) -> {
            Exchange exchange = endpoint.createExchange();
            if (args.length == 1) {
                Object param;
                if (parameterClass != null) {
                    param = PojoUtils.realize(args[0], parameterClass);
                } else {
                    param = args[0];
                }
                Message message = new DefaultMessage(endpoint.getCamelContext());
                message.setBody(param);
                exchange.setMessage(message);
            }
            exchange.getOut().copyFrom(exchange.getIn());
            try {
                getProcessor().process(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return exchange.getOut().getBody();
        };
    }
}
