package io.terminus.dalaran.function.security;

import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.component.utils.AESUtils;
import io.terminus.dalaran.core.component.annotation.MappingFunction;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@MappingFunction(value = "AESEncrypt", description = "AES加密")
public class AESEncrypt {

    public String execute(String data, String secret) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ComponentConstants.AES_PKCS5PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return AESUtils.encrypt(data, cipher);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
