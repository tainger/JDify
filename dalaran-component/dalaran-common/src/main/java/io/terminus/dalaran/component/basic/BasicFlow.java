package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import io.terminus.dalaran.model.FLowCreatedType;
import lombok.Data;

@Data
@DynamicModel(value = "Flow")
public class BasicFlow implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "流程名称", inputType = FieldInputType.String, show = "createdType == \"DataConfig\" || createdType == \"Custom\" || isEditMode")
    private String name;

    @ConfigFieldInfo(label = "创建方式", inputType = FieldInputType.Radio, show = "!isEditMode")
    private FLowCreatedType createdType;

    @ConfigFieldInfo(label = "流程模板", inputType = FieldInputType.TemplateSelector, required = false, show = "createdType == \"Template\" && !isEditMode",
    path = "/api/repository/private/resource", param = "type")
    private String template;

    @ConfigFieldInfo(label = "触发器类型", inputType = FieldInputType.TriggerSelector, required = false, show = "createdType == \"Custom\" || isEditMode",
    path = "/api/platform/trigger")
    private String triggerType;

    @ConfigFieldInfo(label = "配置数据", inputType = FieldInputType.String, required = false, show = "createdType == \"DataConfig\" && !isEditMode")
    private String configData;

    @ConfigFieldInfo(label = "流程描述", inputType = FieldInputType.String, required = false, show = "createdType == \"Custom\" || isEditMode")
    private String description;

    @ConfigFieldInfo(label = "开启日志", inputType = FieldInputType.Switch, defaultValue = "false", show = "createdType == \"Custom\" || isEditMode")
    private boolean tracing = false;
}
