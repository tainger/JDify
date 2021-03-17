package io.terminus.dalaran.component.authenticator;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.Data;

@Data
public class DalaranAuthenticator {

    @ConfigFieldInfo(label = "策略名称", inputType = FieldInputType.String)
    private String name;

    @ConfigFieldInfo(label = "所处位置", inputType = FieldInputType.Select)
    private AuthenticatorKeyLocation keyLocation;

    @ConfigFieldInfo(label = "是否静态", inputType = FieldInputType.Switch, defaultValue = "true")
    private boolean isStatic = true;

    @ConfigFieldInfo(label = "字段名称", inputType = FieldInputType.String)
    private String authenticatorKey;

    @ConfigFieldInfo(label = "字段值", inputType = FieldInputType.String)
    private String authenticatorValue;

    @ConfigFieldInfo(label = "过期时间", inputType = FieldInputType.String)
    private long expireTime;
}
