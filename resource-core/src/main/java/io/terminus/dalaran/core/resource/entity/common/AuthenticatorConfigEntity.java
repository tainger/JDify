package io.terminus.dalaran.core.resource.entity.common;

import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.Data;

@Data
public class AuthenticatorConfigEntity {

    private AuthenticatorKeyLocation keyLocation;

    private Boolean isStatic;

    private String authenticatorKey;

    private String authenticatorValue;

    private long expireTime;
}
