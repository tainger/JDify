package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "SubFlow")
public class BasicSubFlow implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "子流程名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "子流程描述", inputType = FieldInputType.String, required = false)
    private String description;
}
