package io.terminus.dalaran.function.security;

import io.terminus.dalaran.component.utils.AESUtils;
import io.terminus.dalaran.core.component.annotation.MappingFunction;

@MappingFunction(value = "AESEncryptNoPadding", description = "AES加密(NoPadding)")
public class AESEncryptNoPadding {

    public String execute(String data, String secret) {
        return AESUtils.encryptNoPadding(data, secret);
    }

}
