package io.terminus.dalaran.function.security;

import io.terminus.dalaran.component.utils.AESUtils;
import io.terminus.dalaran.core.component.annotation.MappingFunction;

@MappingFunction(value = "AESDecrypt", description = "AES解密")
public class AESDecrypt {

    public String execute(String data, String secret) {
        return AESUtils.decrypt(data, secret);
    }
}
