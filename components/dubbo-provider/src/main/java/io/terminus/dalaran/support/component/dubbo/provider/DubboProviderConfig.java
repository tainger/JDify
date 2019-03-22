package io.terminus.dalaran.support.component.dubbo.provider;

import lombok.Data;

import java.util.List;

@Data
public class DubboProviderConfig {
    private String registryAddress;
    private String serviceId;
    private String method;
    private String version;

    private List<String> parameterTypes;
}