package io.terminus.dalaran.component.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

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
//    public static boolean verify(String text, String sign, String publicKey, SignAlgorithm signAlgorithm, EncryptionAlgorithm encryptionAlgorithm) {
//        try {
//            Signature signature = Signature.getInstance(signAlgorithm.name());
//            PublicKey key = KeyFactory.getInstance(encryptionAlgorithm.name()).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
//            signature.initVerify(key);
//            signature.update(text.getBytes());
//            return signature.verify(Base64.getDecoder().decode(sign));
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("验签失败");
//        }
//        return false;
//    }

    /**
     * 私钥加签
     *
     * @param text       需要签名的字符串
     * @param privateKey 私钥(BASE64编码)
     * @return 签名结果(BASE64编码)
     */
//    public static String sign(String text, String privateKey, SignAlgorithm signAlgorithm, EncryptionAlgorithm encryptionAlgorithm) {
//        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//        try {
//            KeyFactory keyFactory = KeyFactory.getInstance(encryptionAlgorithm.name());
//            PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//            Signature signature = Signature.getInstance(signAlgorithm.name());
//            signature.initSign(privateK);
//            signature.update(text.getBytes());
//            byte[] result = signature.sign();
//            return Base64.getEncoder().encodeToString(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("签名失败");
//        }
//        return null;
//    }

    //计算签字
    public static String calculateMD5Signature(JsonObject jObject, String apiSecret) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String contents = getJsonValue(jObject);
//        signature = Security.md5(contents+apiSecret, "UTF-8");
        return DigestUtils.md5Hex(contents + apiSecret);
    }

    public static String signAES(String body, String secret) {
        return AESUtils.encrypt(body, secret);
    }

    public static boolean verifyAES(String body, String sign, String secret) {
//        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
//        Cipher cipher = Cipher.getInstance(ComponentConstants.AES_PKCS5PADDING);
//        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return StringUtils.equalsIgnoreCase(sign, AESUtils.encrypt(body, secret));
    }

//    public static String buildSignBody(Map<String, Object> in) {
//        Map<String, Object> data = in.entrySet().stream()
//                .filter(entry -> !(StringUtils.equalsIgnoreCase(entry.getKey(), ComponentConstants.SIGNATURE) || StringUtils.equalsIgnoreCase(entry.getKey(), ComponentConstants.SIGNATURE_METHOD)))
//                .sorted((o1, o2) -> StringUtils.compare(o1.getKey(), o2.getKey()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//
//        StringBuilder dataToBeSigned = new StringBuilder();
//        for (Map.Entry entry: data.entrySet()) {
//            dataToBeSigned.append(dataToBeSigned.toString().equals("") ? "" : "&")
//                    .append( entry.getKey() + "=" + entry.getValue());
//        }
//        return dataToBeSigned.toString();
//    }

    protected static String getJsonValue(JsonElement jElement)	{
        StringBuffer value = new StringBuffer("");
        if (jElement.isJsonNull()) {
            //value = "";
        }
        else if (jElement.isJsonArray()) {
            JsonArray jArray = jElement.getAsJsonArray();
            for (int i = 0; i < jArray.size(); i++) {
                value.append(getJsonValue(jArray.get(i)));
            }
        }
        else if (jElement.isJsonObject()) {
            JsonObject jObject = jElement.getAsJsonObject();
            Map.Entry<String, JsonElement>[] keys = jObject.entrySet().toArray(new Map.Entry[jObject.entrySet().size()]);
            Arrays.sort(keys, Comparator.comparing(Map.Entry::getKey));
            for (Map.Entry<String, JsonElement> key : keys) {
                if (key.getKey().equalsIgnoreCase("apiKey") || key.getKey().equalsIgnoreCase("signature") || key.getValue().isJsonNull()) {
                    continue;
                }
                value.append(getJsonValue(key.getValue())) ;
            }
        }
        else {
            value.append(jElement.getAsString()) ;
        }
        return value.toString();
    }
}