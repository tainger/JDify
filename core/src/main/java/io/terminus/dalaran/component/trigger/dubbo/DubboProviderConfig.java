package io.terminus.dalaran.trigger.dubbo;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class DubboProviderConfig {

    @ConfigFieldInfo(label = "注册中心地址", inputType = FieldInputType.String)
    private String registryAddress;
    @ConfigFieldInfo(label = "服务 ID", inputType = FieldInputType.String)
    private String serviceId;
    @ConfigFieldInfo(label = "服务方法", inputType = FieldInputType.String)
    private String method;
    @ConfigFieldInfo(label = "服务版本", inputType = FieldInputType.String)
    private String version;
}