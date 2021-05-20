package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import io.terminus.dalaran.model.SubFlowCreateType;
import lombok.Data;

@Data
@DynamicModel(value = "SubFlow")
public class BasicSubFlow implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "子流程名称", inputType = FieldInputType.String, show = "createdType == \"Custom\" || isEditMode")
    private String name;

    @ConfigFieldInfo(label = "创建方式", inputType = FieldInputType.Radio, show = "!isEditMode")
    private SubFlowCreateType createdType;

    @ConfigFieldInfo(label = "子流程模板", inputType = FieldInputType.SubFlowTemplateSelector, required = false, show = "createdType == \"Template\" && !isEditMode",
    path = "/api/repository/private/resource", param = "type")
    private String template;

    @ConfigFieldInfo(label = "子流程描述", inputType = FieldInputType.String, required = false, show = "createdType == \"Custom\" || isEditMode")
    private String description;
}