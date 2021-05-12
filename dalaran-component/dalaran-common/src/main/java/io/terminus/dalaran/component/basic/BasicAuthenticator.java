package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicModel;
import lombok.Data;

@Data
@DynamicModel(value = "Authenticator")
public class BasicAuthenticator implements DalaranBasicComponent {

    @ConfigFieldInfo(label = "策略名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "鉴权器类型", inputType = FieldInputType.AuthenticatorSelector, dynamic = true,
            path = "/api/platform/authenticator")
    private String type;

}
