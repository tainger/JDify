package io.terminus.dalaran.camel.component.dubbo;

import com.alibaba.dubbo.common.bytecode.ClassGenerator;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import javassist.*;
import javassist.util.proxy.ProxyFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultMessage;

import java.lang.reflect.InvocationTargetException;

import static io.terminus.dalaran.camel.component.dubbo.DubboCamelConstants.*;

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
        Class interfaceClass = getClass(endpoint.getServiceId());
        if (interfaceClass == null) {
            interfaceClass = createClass();
        }
        ServiceConfig provider = new ServiceConfig();
        provider.setApplication(new ApplicationConfig("test"));
        provider.setRegistry(new RegistryConfig(endpoint.getRegistryAddress()));
        provider.setVersion(endpoint.getVersion());
        provider.setInterface(interfaceClass);
        provider.setRef(createProxyBean(interfaceClass));
        return provider;
    }

    private Object createProxyBean(Class interfaceClass) {
        ProxyFactory serviceProxy = new ProxyFactory();
        serviceProxy.setInterfaces(new Class[]{interfaceClass});
        serviceProxy.setFilter(m -> endpoint.getMethod().equals(m.getName()));
        try {
            return serviceProxy.create(new Class<?>[0], new Object[0], (self, thisMethod, proceed, args) -> {
                Exchange exchange = endpoint.createExchange();
                exchange.setProperty(DUBBO_SERVICE_ID_HEADER, endpoint.getServiceId());
                exchange.setProperty(DUBBO_SERVICE_METHOD_HEADER, endpoint.getMethod());
                exchange.setProperty(DUBBO_SERVICE_VERSION_HEADER, endpoint.getVersion());
                exchange.setProperty(DUBBO_REGISTRY_ADDRESS_HEADER, endpoint.getRegistryAddress());
                exchange.setProperty(DUBBO_PARAMETER_TYPES_HEADER, endpoint.getParameterTypes());
                Message message = new DefaultMessage(endpoint.getCamelContext());
                message.setBody(args);
                exchange.setMessage(message);
                getProcessor().process(exchange);
                return exchange.getOut().getBody();
            });
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private Class createClass() {
        // TODO 这里有问题, 一个interface 的多个方法会挂, 因为第一次的时候类以及生成了, 理论上可以通过独立 classloader 的方式解决, 类已存在但是方法不存在的情况下, 可以删除 classloader 重新整合 create class
        try {
            ClassPool pool = ClassGenerator.getClassPool(Thread.currentThread().getContextClassLoader());
            CtClass dubboInterfaceCtClass = pool.getOrNull(endpoint.getServiceId());
            if (dubboInterfaceCtClass == null) {
                dubboInterfaceCtClass = pool.makeInterface(endpoint.getServiceId());
            }
            CtMethod method = CtNewMethod.make("public Object " + endpoint.getMethod() + "();", dubboInterfaceCtClass);
            dubboInterfaceCtClass.addMethod(method);
            Class dubboInterface = dubboInterfaceCtClass.toClass();
            return dubboInterface;
        } catch (CannotCompileException e) {
            e.printStackTrace();
            return null;
        }
    }
}
