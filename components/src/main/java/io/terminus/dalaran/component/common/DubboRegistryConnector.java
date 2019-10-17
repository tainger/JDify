package io.terminus.dalaran.component.common;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class DubboRegistryConnector {

    @ConfigFieldInfo(label = "注册中心地址", inputType = FieldInputType.String, defaultValue = "zookeeper://${host}:${port}")
    private String address;

    @ConfigFieldInfo(label = "应用名", inputType = FieldInputType.String)
    private String application;
}
