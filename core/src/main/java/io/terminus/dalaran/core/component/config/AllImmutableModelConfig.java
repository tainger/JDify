package io.terminus.dalaran.core.component.config;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class AllImmutableModelConfig extends ImmutableModelConfig {

    @ConfigFieldInfo(label = "输入结构", inputType = FieldInputType.Model, readonly = true)
    private String inModelId;

    @ConfigFieldInfo(label = "输出结构", inputType = FieldInputType.Model, readonly = true)
    private String outModelId;
}
