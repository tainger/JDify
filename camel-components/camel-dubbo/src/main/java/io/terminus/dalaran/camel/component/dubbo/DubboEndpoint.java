package io.terminus.dalaran.camel.component.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.impl.ProcessorEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

@UriEndpoint(firstVersion = "1.0.0", scheme = "dubbo", title = "DUBBO", syntax = "dubbo:registryAddress", label = "rpc")
public class DubboEndpoint extends ProcessorEndpoint {

    @UriParam(description = "Dubbo registry address", javaType = "java.lang.String")
    @Metadata(required = "true")
    private String registryAddress;
    @UriParam(description = "Dubbo service ID", javaType = "java.lang.String")
    @Metadata(required = "true")
    private String serviceId;
    @UriParam(description = "Dubbo service method", javaType = "java.lang.String")
    @Metadata(required = "true")
    private String method;
    @UriParam(defaultValue = "1.0.0", description = "Dubbo service version", javaType = "java.lang.String")
    private String version;
    @UriParam(label = "parameterType", description = "Dubbo service parameter type", javaType = "java.lang.String")
    private String parameterType;

    private GenericService genericService;

    @Override
    protected Processor createProcessor() {
        return new DubboCamelProcessor(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        System.out.println("createConsumer start");
        return new DubboCamelConsumer(this, processor);
    }

    public GenericService getDubboConsumerService() {
        if (genericService == null) {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            reference.setApplication(new ApplicationConfig("test"));
            reference.setRegistry(new RegistryConfig(registryAddress));
            reference.setVersion(version);
            reference.setInterface(serviceId); // 接口名
            reference.setGeneric(true); // 声明为泛化接口

            //ReferenceConfig实例很重，封装了与注册中心的连接以及与提供者的连接，
            //需要缓存，否则重复生成ReferenceConfig可能造成性能问题并且会有内存和连接泄漏。
            //API方式编程时，容易忽略此问题。
            //这里使用dubbo内置的简单缓存工具类进行缓存
            ReferenceConfigCache cache = ReferenceConfigCache.getCache();
            genericService = cache.get(reference);
        }
        return this.genericService;
    }

    @Override
    protected String createEndpointUri() {
        return "dubbo://" + serviceId + "." + method;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public GenericService getGenericService() {
        return genericService;
    }

}
