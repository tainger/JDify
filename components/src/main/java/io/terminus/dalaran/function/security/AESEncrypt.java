package io.terminus.dalaran.function.security;

import io.terminus.dalaran.component.utils.AESUtils;
import io.terminus.dalaran.core.component.annotation.MappingFunction;

@MappingFunction(value = "AESEncrypt", description = "AES加密")
public class AESEncrypt {

    public String execute(String data, String secret) {
        return AESUtils.encrypt(data, secret);
    }
}
