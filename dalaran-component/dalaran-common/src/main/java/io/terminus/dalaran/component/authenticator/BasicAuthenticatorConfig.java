package io.terminus.dalaran.component.authenticator;

import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.Data;

@Data
public class BasicAuthenticatorConfig {

    private AuthenticatorKeyLocation keyLocation;

    private Boolean isStatic = true;

    private String authenticatorKey;

    private String authenticatorValue;

    private String expireTime;
}
