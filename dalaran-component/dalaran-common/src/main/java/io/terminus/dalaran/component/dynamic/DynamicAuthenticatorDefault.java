package io.terminus.dalaran.component.dynamic;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.DalaranDynamicConfig;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.DynamicConfig;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.Data;

@Data
@DynamicConfig(value = "AuthenticatorTypeDefault", name = "AuthenticatorType", type = "Default", origin = "basic")
public class DynamicAuthenticatorDefault implements DalaranDynamicConfig {

    @ConfigFieldInfo(label = "所处位置", inputType = FieldInputType.Select)
    private AuthenticatorKeyLocation keyLocation;

    @ConfigFieldInfo(label = "是否静态", inputType = FieldInputType.Switch, defaultValue = "true")
    private Boolean isStatic;

    @ConfigFieldInfo(label = "字段名称", inputType = FieldInputType.String)
    private String authenticatorKey;

    @ConfigFieldInfo(label = "字段值", inputType = FieldInputType.String)
    private String authenticatorValue;

    @ConfigFieldInfo(label = "过期时间", inputType = FieldInputType.String)
    private long expireTime;
}
