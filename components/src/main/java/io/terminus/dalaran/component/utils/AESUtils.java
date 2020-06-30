package io.terminus.dalaran.component.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class AESUtils {

    public static String encrypt(String origin, String secret) {
        if (StringUtils.isBlank(origin) || StringUtils.isBlank(secret)) {
            return null;
        }
        try {
            byte[] key = secret.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] digestBytes = sha.digest(key);
            byte[] secretBytes = Arrays.copyOf(digestBytes, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(origin.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AES Encrypt Error: " + e.getMessage());
        }
    }

    public static String decrypt(String origin, String secret) {
        if (StringUtils.isBlank(origin) || StringUtils.isBlank(secret)) {
            return null;
        }
        try {
            byte[] key = secret.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] digestBytes = sha.digest(key);
            byte[] secretBytes = Arrays.copyOf(digestBytes, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(origin)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AES Decrypt Error: " + e.getMessage());
        }
    }
}
