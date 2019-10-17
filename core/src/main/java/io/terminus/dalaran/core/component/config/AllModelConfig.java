package io.terminus.dalaran.core.component.config;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class AllModelConfig extends ComponentModelConfig {

    @ConfigFieldInfo(label = "输入结构", inputType = FieldInputType.Model)
    private Long inModelId;

    @ConfigFieldInfo(label = "输出结构", inputType = FieldInputType.Model)
    private Long outModelId;
}
