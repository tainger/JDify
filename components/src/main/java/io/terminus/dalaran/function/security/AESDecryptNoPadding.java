package io.terminus.dalaran.function.security;


import io.terminus.dalaran.component.utils.AESUtils;
import io.terminus.dalaran.core.component.annotation.MappingFunction;

@MappingFunction(value = "AESDecryptNoPadding", description = "AES解密(NoPadding)")
public class AESDecryptNoPadding {

    public String execute(String data, String secret) {
        return AESUtils.decryptNoPadding(data, secret);
    }
}
