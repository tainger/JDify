package io.terminus.dalaran.function.security;

import io.terminus.dalaran.component.utils.AESUtils;
import io.terminus.dalaran.core.component.annotation.MappingFunction;

@MappingFunction(value = "AESDecrypt", description = "AES解密")
public class AESDecrypt {

    public String execute(String data, String secret) {
        try {
//            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
//            Cipher cipher = Cipher.getInstance(ComponentConstants.AES_PKCS5PADDING);
//            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return AESUtils.decrypt(data, secret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
