package io.terminus.dalaran.core.component.config;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class ComponentBasicConfig {

    @ConfigFieldInfo(label = "节点名称", inputType = FieldInputType.String)
    private String name;
}
