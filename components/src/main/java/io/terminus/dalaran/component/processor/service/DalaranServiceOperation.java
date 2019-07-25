package io.terminus.dalaran.component.processor.service;

import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ImmutableModelConfig;
import io.terminus.dalaran.model.component.ServiceOperation;
import lombok.Data;

@Data
public class DalaranServiceOperation extends ImmutableModelConfig {

    @ConfigFieldInfo(label = "服务", inputType = FieldInputType.Service)
    private DalaranService dalaranService;

    @ConfigFieldInfo(label = "操作", inputType = FieldInputType.ServiceOperation)
    private ServiceOperation operationConfig;
}
