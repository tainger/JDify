package io.terminus.dalaran.component.processor.script;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class DalaranScriptConfig extends OutModelConfig {

    @ConfigFieldInfo(label = "脚本语言", inputType = FieldInputType.Radio, defaultValue = "JavaScript")
    private DalaranScriptType type;

    @ConfigFieldInfo(label = "脚本代码", inputType = FieldInputType.Script)
    private String script;
}
