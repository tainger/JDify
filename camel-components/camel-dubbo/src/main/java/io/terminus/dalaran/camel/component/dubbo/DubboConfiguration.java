package io.terminus.dalaran.camel.component.dubbo;

import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;
import org.apache.camel.spi.UriPath;

import java.util.List;

@UriParams
public class DubboConfiguration {

    @UriPath(description = "Dubbo registry address", javaType = "java.lang.String")
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
    @UriParam(label = "parameterType", description = "Dubbo service parameter types", javaType = "java.lang.String")
    private List<String> parameterTypes;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
