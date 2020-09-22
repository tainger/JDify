package io.terminus.dalaran.component.utils;

import io.terminus.dalaran.ComponentConstants;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class AESUtils {

    public static String encrypt(String origin, Cipher cipher) {
        if (StringUtils.isBlank(origin)) {
            return null;
        }
        try {
//            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
//            Cipher cipher = Cipher.getInstance(ComponentConstants.AES_PKCS5PADDING);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(origin.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AES Encrypt Error: " + e.getMessage());
        }
    }

    public static String decrypt(String origin, Cipher cipher) {
        if (StringUtils.isBlank(origin)) {
            return null;
        }
        try {
//            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
//            Cipher cipher = Cipher.getInstance(ComponentConstants.AES_PKCS5PADDING);
//            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(origin)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AES Decrypt Error: " + e.getMessage());
        }
    }

    public static String encryptNoPadding(String data, String secret) {
        try {
//            val key = fillAESZeroPadding(secret, StandardCharsets.UTF_8);
            val secretKeySpec = new SecretKeySpec(secret.getBytes(), "AES");
            var cipher = Cipher.getInstance(ComponentConstants.AES_NOPADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            val content = fillAESZeroPadding(data, StandardCharsets.UTF_8);
            val result = cipher.doFinal(content);
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptNoPadding(String data, String secret) {
        try {
//            val key = fillAESZeroPadding(secret, StandardCharsets.UTF_8);
            val content = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
            val secretKeySpec = new SecretKeySpec(secret.getBytes(), "AES");
            var cipher = Cipher.getInstance(ComponentConstants.AES_NOPADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            var raw = cipher.doFinal(content);
            val newRaw = removeZeroPadding(raw);
            return new String(newRaw, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] fillAESZeroPadding(String pwd , Charset charset) {
        val password = pwd.getBytes(charset);
        val size = password.length;
        val factor = (size/16 + 1) * 16;

        val offset = size % factor;
        if(offset == 0) {
            return password;
        }
        val realSize = size + (factor - offset);
        val key = new byte[realSize];
        System.arraycopy(password, 0, key, 0, size);
        return key;
    }

    private static byte[] removeZeroPadding(byte[] bytes) {
        var newLength = bytes.length;
        val length = bytes.length;
        for (int i =  length - 1; i >= 0; i--) {
            val b = bytes[i];
            if (Integer.valueOf(b) != 0) {
                break;
            }
            newLength--;
        }
        if (newLength == length) {
            return bytes;
        }
        val newBytes = new byte[newLength];
        System.arraycopy(bytes, 0, newBytes, 0, newLength);
        return newBytes;
    }
}
