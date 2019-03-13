package io.terminus.dalaran.component.dubbo.provider;

import lombok.Data;

@Data
public class DubboProviderConfig {
    private String registryAddress;
    private String serviceId;
    private String method;
    private String version;
}