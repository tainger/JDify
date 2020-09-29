package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "Flow")
public class BasicTrigger implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "流程名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "触发器类型", inputType = FieldInputType.TriggerSelector)
    private String triggerType;

    @ConfigFieldInfo(label = "流程描述", inputType = FieldInputType.String)
    private String description;

    @ConfigFieldInfo(label = "开启日志", inputType = FieldInputType.Switch)
    private boolean tracing;

}
