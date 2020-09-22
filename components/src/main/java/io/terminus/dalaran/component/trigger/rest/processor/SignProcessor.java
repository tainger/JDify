package io.terminus.dalaran.component.trigger.rest.processor;

import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.component.trigger.rest.model.SignAuthenticatorInfo;
import io.terminus.dalaran.component.utils.AESUtils;
import io.terminus.dalaran.component.utils.SignUtils;
import lombok.Data;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static io.terminus.dalaran.DalaranConstants.AUTH_APP_KEY;
import static io.terminus.dalaran.DalaranConstants.AUTH_APP_SECRET;
import static io.terminus.dalaran.component.trigger.rest.utils.SignUtils.stopExchangeOnInvalidAppKey;
import static io.terminus.dalaran.component.trigger.rest.utils.SignUtils.stopExchangeOnMissingAppKey;

@Data
public class SignProcessor implements Processor {
    private Map<String, String> clientMapper;
    private SignAuthenticatorInfo authenticatorInfo;

    public SignProcessor(Map<String, String> clientMapper, SignAuthenticatorInfo authenticatorInfo) {
        this.clientMapper = clientMapper;
        this.authenticatorInfo = authenticatorInfo;
    }

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        checkSign(exchange, body, authenticatorInfo);
    }

    void checkSign(Exchange exchange, Map<String, Object> body, SignAuthenticatorInfo authenticatorInfo) {
        String in = buildSignBody(body);
        if (!body.containsKey(ComponentConstants.SIGNATURE)) {
            stopExchangeOnInvalidAppKey(exchange);
            return;
        }
        String sign = body.get(ComponentConstants.SIGNATURE).toString();
        if (!verify(in, sign, authenticatorInfo)) {
            stopExchangeOnInvalidAppKey(exchange);
            return;
        }
        exchange.getOut().setBody(body);
    }

    void checkSign(Exchange exchange, Map<String, String> body) {
        String appKey = body.get(AUTH_APP_KEY);
        if (StringUtils.isEmpty(appKey)) {
            stopExchangeOnMissingAppKey(exchange);
            return;
        }
//        String sign = body.get(AUTH_SIGN);
//        if (StringUtils.isEmpty(sign)) {
//            stopExchangeOnMissingSign(exchange);
//            return;
//        }
        String secret = clientMapper.get(appKey);
        if (StringUtils.isEmpty(secret) || !secret.equals(body.get(AUTH_APP_SECRET))) {
            stopExchangeOnInvalidAppKey(exchange);
            return;
        }
        setOutBody(exchange, body);

//        Map<String, String> sortedBody = new TreeMap<>(body);
//        sortedBody.remove(AUTH_SIGN);

//        String bodyString = sortedBody.entrySet().stream()
//                .map(entry -> entry.getKey() + "=" + entry.getValue())
//                .collect(Collectors.joining("&")) + secret;

//        if (SignUtils.signEquals(bodyString, sign)) {
//            setOutBody(exchange, body);
//            return;
//        }
//        stopExchangeOnInvalidSign(exchange);
    }

    public boolean verify(String body, String sign, SignAuthenticatorInfo authenticator) {
        return SignUtils.verify(body, sign, authenticator.getPartnerPublicKey(), authenticator.getSignAlgorithm(), authenticator.getEncryptionAlgorithm());
    }

    public String sign(String body, SignAuthenticatorInfo authenticator) {
        return SignUtils.sign(body, authenticator.getDalaranPrivateKey(), authenticator.getSignAlgorithm(), authenticator.getEncryptionAlgorithm());
    }

    public String signAES(String body, SecretKeySpec secretKeySpec) throws Exception {
        Cipher cipher = Cipher.getInstance(ComponentConstants.AES_PKCS5PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return AESUtils.encrypt(body, cipher);
    }

    public boolean verifyAES(String body, String sign, String secret) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance(ComponentConstants.AES_PKCS5PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return StringUtils.equalsIgnoreCase(sign, AESUtils.encrypt(body, cipher));
    }

    public String buildSignBody(Map<String, Object> in) {
        Map<String, Object> data = in.entrySet().stream()
                .filter(entry -> !(StringUtils.equalsIgnoreCase(entry.getKey(), ComponentConstants.SIGNATURE) || StringUtils.equalsIgnoreCase(entry.getKey(), ComponentConstants.SIGNATURE_METHOD)))
                .sorted((o1, o2) -> StringUtils.compare(o1.getKey(), o2.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        StringBuilder dataToBeSigned = new StringBuilder();
        for (Map.Entry entry: data.entrySet()) {
            dataToBeSigned.append(dataToBeSigned.toString().equals("") ? "" : "&")
                    .append( entry.getKey() + "=" + entry.getValue());
        }
        return dataToBeSigned.toString();
    }

    void setOutBody(Exchange exchange, Map<String, String> body) {
        exchange.getOut().setBody(body.get("data"));
    }
}
