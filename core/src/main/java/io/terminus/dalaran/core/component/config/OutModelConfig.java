package io.terminus.dalaran.core.component.config;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class OutModelConfig extends ComponentModelConfig {

    @ConfigFieldInfo(label = "输出结构", inputType = FieldInputType.Model, required = false)
    private Long outModelId;
}
