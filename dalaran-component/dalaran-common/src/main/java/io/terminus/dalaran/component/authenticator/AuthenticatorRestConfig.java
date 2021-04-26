package io.terminus.dalaran.component.authenticator;

import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.Data;

@Data
public class AuthenticatorRestConfig {

    private AuthenticatorKeyLocation keyLocation;

    private Boolean isStatic;

    private String authenticatorKey;

    private String authenticatorValue;

    private long expireTime;
}
