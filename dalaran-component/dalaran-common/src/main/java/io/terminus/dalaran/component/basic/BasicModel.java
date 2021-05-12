package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "Model")
public class BasicModel implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "类型", inputType = FieldInputType.ModelSelector, path = "/api/platform/model")
    private String modelType;
}
