package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.Data;

@Data
public class SignAuthenticatorConfigDTO {

    private String appKey;

    private String appSecret;

    private AuthenticatorKeyLocation signLocation;

}
