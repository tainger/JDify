package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.processor.script.DalaranScriptType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "Function")
public class BasicFunction implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "函数名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "函数引擎", inputType = FieldInputType.Select)
    private DalaranScriptType type;

    @ConfigFieldInfo(label = "函数入参", inputType = FieldInputType.String)
    private String[] params;

    @ConfigFieldInfo(label = "函数脚本", inputType = FieldInputType.String)
    private String script;

    @ConfigFieldInfo(label = "函数描述", inputType = FieldInputType.String, required = false)
    private String description;
}
