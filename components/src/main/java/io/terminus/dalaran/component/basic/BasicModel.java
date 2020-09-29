package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import io.terminus.dalaran.model.annotation.ModelFieldInfo;
import lombok.Data;

@Data
@DynamicModel(value = "Model")
public class BasicModel implements DalaranBasicComponent {

    @ModelFieldInfo(label = "名称", inputType = FieldInputType.String)
    private String name;

    @ModelFieldInfo(label = "类型", inputType = FieldInputType.ModelSelector)
    private String modelType;
}
