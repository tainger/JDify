package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "Processor")
public class BasicProcessor implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "展示名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "节点类型", inputType = FieldInputType.ProcessorSelector, path = "/api/platform/processor", dynamic = true)
    private String type;
}
