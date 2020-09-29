package io.terminus.dalaran.component.basic;


import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "Service")
public class BasicService implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "服务名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "服务类型", inputType = FieldInputType.ServiceSelector)
    private String type;

    @ConfigFieldInfo(label = "服务描述", inputType = FieldInputType.String)
    private String description;

    @ConfigFieldInfo(label = "所属模块", inputType = FieldInputType.ModuleSelector)
    private Long moduleId;
}
