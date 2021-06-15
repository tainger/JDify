package io.terminus.dalaran.component.authenticator;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.Authenticator;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.Data;

@Data
@Authenticator(value = "Sign")
public class AuthenticatorSign {

    @ConfigFieldInfo(label = "签名字段", inputType = FieldInputType.String)
    private String appKey;

    @ConfigFieldInfo(label = "签名密钥", inputType = FieldInputType.Password)
    private String appSecret;

    @ConfigFieldInfo(label = "签名位置", inputType = FieldInputType.Select)
    private AuthenticatorKeyLocation signLocation;
}
