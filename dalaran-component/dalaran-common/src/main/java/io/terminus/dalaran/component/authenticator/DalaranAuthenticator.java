package io.terminus.dalaran.component.authenticator;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.Authenticator;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.Data;

import java.util.List;

@Data
@Authenticator(value = "BasicAuthenticator")
public class DalaranAuthenticator<T> {

    private String type;

    private List<T> config;

    @ConfigFieldInfo(label = "所处位置", inputType = FieldInputType.Select)
    private AuthenticatorKeyLocation keyLocation;

    @ConfigFieldInfo(label = "是否静态", inputType = FieldInputType.Switch, defaultValue = "true")
    private Boolean isStatic = true;

    @ConfigFieldInfo(label = "字段名称", inputType = FieldInputType.String)
    private String authenticatorKey;

    @ConfigFieldInfo(label = "字段值", inputType = FieldInputType.String)
    private String authenticatorValue;

    @ConfigFieldInfo(label = "过期时间", inputType = FieldInputType.String, show = "isStatic == false", dynamic = true)
    private String expireTime;

}
