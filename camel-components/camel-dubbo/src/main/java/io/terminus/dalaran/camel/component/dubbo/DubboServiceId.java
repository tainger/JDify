package io.terminus.dalaran.camel.component.dubbo;

import com.alibaba.dubbo.common.utils.StringUtils;

import java.util.Objects;

public class DubboServiceId {

    private String registryAddress;

    private String serviceId;

    private String version;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DubboServiceId)) {
            return false;
        }
        DubboServiceId dubboServiceId = (DubboServiceId) obj;
        return StringUtils.isEquals(registryAddress, dubboServiceId.registryAddress) &&
                StringUtils.isEquals(serviceId, dubboServiceId.serviceId) &&
                StringUtils.isEquals(version, dubboServiceId.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registryAddress, serviceId, version);
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
