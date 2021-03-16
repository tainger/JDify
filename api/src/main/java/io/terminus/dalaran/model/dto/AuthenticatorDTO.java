package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import io.terminus.dalaran.model.dto.basic.BasicAuthenticatorInfo;
import lombok.Data;

@Data
public class AuthenticatorDTO extends BasicAuthenticatorInfo {

    private AuthenticatorKeyLocation keyLocation;

    private boolean isStatic;

    private String authenticatorKey;

    private String authenticatorValue;

    private String expireTime;
}
