package io.terminus.dalaran.component.processor.context;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class SetContextConfig {

    @ConfigFieldInfo(label = "上下文标识", inputType = FieldInputType.String)
    private String key;

    @ConfigFieldInfo(label = "参数值", inputType = FieldInputType.String)
    private String value;
}
