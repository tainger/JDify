package io.terminus.dalaran.model;

import lombok.Data;

@Data
public class AuthenticatorKeyResponse {

    private String authenticatorKey;

    public AuthenticatorKeyResponse(String authenticatorKey) {
        this.authenticatorKey = authenticatorKey;
    }

}
