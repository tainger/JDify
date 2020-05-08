package io.terminus.dalaran.component.utils;

import io.terminus.dalaran.component.trigger.rest.model.EncryptionAlgorithm;
import io.terminus.dalaran.component.trigger.rest.model.SignAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class SignUtils {

    /** 签名算法 **/
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    /** 加密算法 **/
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 公钥验签
     *
     * @param text      原字符串
     * @param sign      签名结果
     * @param publicKey 公钥
     * @return 验签结果
     */
    public static boolean verify(String text, String sign, String publicKey, SignAlgorithm signAlgorithm, EncryptionAlgorithm encryptionAlgorithm) {
        try {
            Signature signature = Signature.getInstance(signAlgorithm.name());
            PublicKey key = KeyFactory.getInstance(encryptionAlgorithm.name()).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
            signature.initVerify(key);
            signature.update(text.getBytes());
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("验签失败");
        }
        return false;
    }

    /**
     * 私钥加签
     *
     * @param text       需要签名的字符串
     * @param privateKey 私钥(BASE64编码)
     * @return 签名结果(BASE64编码)
     */
    public static String sign(String text, String privateKey, SignAlgorithm signAlgorithm, EncryptionAlgorithm encryptionAlgorithm) {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(encryptionAlgorithm.name());
            PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance(signAlgorithm.name());
            signature.initSign(privateK);
            signature.update(text.getBytes());
            byte[] result = signature.sign();
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("签名失败");
        }
        return null;
    }
}