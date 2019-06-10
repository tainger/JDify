package io.terminus.dalaran.component.processor.service;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ComponentModelConfig;
import lombok.Data;

@Data
public class ServiceOperationConfig extends ComponentModelConfig {

    @ConfigFieldInfo(label = "服务", inputType = FieldInputType.Service)
    private Long serviceId;

    @ConfigFieldInfo(label = "操作", inputType = FieldInputType.ServiceOperation)
    private String operation;
}
