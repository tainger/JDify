package io.terminus.dalaran.component.trigger.trantor;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllImmutableModelConfig;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import lombok.Data;

@Data
public class TrantorTriggerConfig extends AllImmutableModelConfig {

    @ConfigFieldInfo(label = "所属模块", inputType = FieldInputType.Select)
    private String module;

    @ConfigFieldInfo(label = "集成点", inputType = FieldInputType.Select)
    private String key;

    @ConfigFieldInfo(label = "集成方法", inputType = FieldInputType.Select)
    private String method;
}
