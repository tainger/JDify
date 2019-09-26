package io.terminus.dalaran.component.processor.service;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ImmutableModelConfig;
import lombok.Data;

@Data
public class ServiceOperationConfig extends ImmutableModelConfig {

    @ConfigFieldInfo(label = "服务", inputType = FieldInputType.Service)
    private Long serviceId;

    @ConfigFieldInfo(label = "操作", inputType = FieldInputType.ServiceOperation)
    private String operation;
}
