package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "Client")
public class BasicClient implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "应用名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "App Key", inputType = FieldInputType.String)
    private String appKey;

    @ConfigFieldInfo(label = "App Secret", inputType = FieldInputType.Password)
    private String secret;

    @ConfigFieldInfo(label = "应用描述", inputType = FieldInputType.String, required = false)
    private String description;
}
