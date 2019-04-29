package io.terminus.dalaran.component.processor.script;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.ModelOptionalConfig;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class DalaranScriptConfig extends ModelOptionalConfig {

    @ConfigFieldInfo(label = "脚本语言", inputType = FieldInputType.Radio)
    private DalaranScriptType type;

    @ConfigFieldInfo(label = "脚本代码", inputType = FieldInputType.String)
    private String script;
}
