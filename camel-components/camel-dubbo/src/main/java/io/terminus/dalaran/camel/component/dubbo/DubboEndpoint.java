package io.terminus.dalaran.camel.component.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.impl.ProcessorEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

@UriEndpoint(firstVersion = "1.0.0", scheme = "dubbo", title = "Dubbo", syntax = "dubbo:application", label = "rpc")
public class DubboEndpoint extends ProcessorEndpoint {

    @UriParam(description = "Dubbo application name", javaType = "java.lang.String")
    @Metadata(required = "true")
    private String application;

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
    @UriParam(defaultValue = "500", description = "Dubbo service timeout", javaType = "java.lang.Integer")
    private Integer timeout;
    @UriParam(label = "parameterType", description = "Dubbo service parameter type", javaType = "java.lang.String")
    private String parameterType;

    private GenericService genericService;

    private ApplicationConfig applicationConfig;

    private DalaranDubboContext dalaranDubboContext;

    public DubboEndpoint(DalaranDubboContext dalaranDubboContext) {
        this.dalaranDubboContext = dalaranDubboContext;
    }

    @Override
    protected Processor createProcessor() {
        return new DubboCamelProcessor(this);
    }

    public GenericService getGenericService() {
        if (genericService == null) {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            reference.setApplication(applicationConfig);
            reference.setRegistry(new RegistryConfig(registryAddress));
            reference.setVersion(version);
            reference.setTimeout(timeout);
            reference.setInterface(serviceId);
            reference.setCheck(false);
            reference.setGeneric(true);
            genericService = reference.get();
        }
        return this.genericService;
    }

    public String getApplication() {
        return application;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        System.out.println("createConsumer start");
        DubboGenericProvider genericProvider = dalaranDubboContext.getProvider(registryAddress, serviceId, version);
        if (genericProvider == null) {
            genericProvider = dalaranDubboContext.createProvider(this);
        }
        return new DubboCamelConsumer(this, processor, genericProvider);
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public void setApplication(String application) {
        this.application = application;
        this.applicationConfig = new ApplicationConfig(application);

        RegistryConfig registryConfig = new RegistryConfig(this.registryAddress);
        this.applicationConfig.setRegistry(registryConfig);
        this.applicationConfig.setQosEnable(false);
    }

    @Override
    protected String createEndpointUri() {
        return "dubbo://" + application + "." + serviceId + "." + method;
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

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }
}
