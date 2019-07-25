package io.terminus.dalaran.component.trigger.trantor;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import lombok.Data;

@Data
public class TrantorTriggerConfig extends AllModelConfig {

    @ConfigFieldInfo(label = "集成点 Key", inputType = FieldInputType.Trantor)
    private String key;

    @ConfigFieldInfo(label = "集成点 Method", inputType = FieldInputType.TrantorMethod)
    private String method;
}
