package io.terminus.dalaran.component.authenticator;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.Authenticator;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

import java.util.List;

@Data
@Authenticator(value = "BasicAuthenticator")
public class DalaranAuthenticator<T> {

    @ConfigFieldInfo(label = "策略名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "鉴权器类型", inputType = FieldInputType.AuthenticatorSelector)
    private String type;

    private List<T> config;
}
