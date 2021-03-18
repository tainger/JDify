package io.terminus.dalaran.model;

import lombok.Data;

@Data
public class AuthenticatorValueResponse {

    private String authenticatorValue;

    public AuthenticatorValueResponse(String authenticatorValue) {
        this.authenticatorValue = authenticatorValue;
    }
}
