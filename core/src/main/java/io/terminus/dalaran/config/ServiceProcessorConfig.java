package io.terminus.dalaran.config;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class ServiceProcessorConfig {
    @ConfigFieldInfo(label = "调用服务", inputType = FieldInputType.Service)
    private Long serviceId;
}
