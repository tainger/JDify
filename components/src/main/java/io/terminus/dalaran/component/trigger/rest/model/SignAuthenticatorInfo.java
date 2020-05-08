package io.terminus.dalaran.component.trigger.rest.model;

import lombok.Data;

@Data
public class SignAuthenticatorInfo {

    private String dalaranPublicKey;

    private String dalaranPrivateKey;

    private String partnerPublicKey;

    private EncryptionAlgorithm encryptionAlgorithm;

    private SignAlgorithm signAlgorithm;
}
