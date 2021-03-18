package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.Data;

@Data
public class AuthenticatorConfigDTO {

    private AuthenticatorKeyLocation keyLocation;

    private Boolean isStatic;

    private String authenticatorKey;

    private String authenticatorValue;

    private long expireTime;
}
