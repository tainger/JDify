package io.terminus.dalaran.component.processor.script;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class DalaranScriptConfig {

    @ConfigFieldInfo(label = "脚本语言", inputType = FieldInputType.Radio, isEnum = true)
    private DalaranScriptType type;

    @ConfigFieldInfo(label = "脚本代码", inputType = FieldInputType.String)
    private String script;
}
