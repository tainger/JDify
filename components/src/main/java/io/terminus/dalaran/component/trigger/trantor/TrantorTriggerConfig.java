package io.terminus.dalaran.component.trigger.trantor;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import lombok.Data;

@Data
public class TrantorTriggerConfig extends AllModelConfig {

    @ConfigFieldInfo(label = "川陀 Action 的 Key", inputType = FieldInputType.String)
    private String key;

    @ConfigFieldInfo(label = "川陀 Action 的 Method name", inputType = FieldInputType.String)
    private String method;
}
