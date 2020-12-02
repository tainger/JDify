package io.terminus.dalaran.component.http.trigger.model;

import lombok.Data;

import static io.terminus.dalaran.DalaranConstants.AUTH_APP_KEY;

@Data
public class SignBodyModel {

    private String appKey;

    private String data;

    private String sign;

    private long timestamp;

    String buildString(String secret) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(AUTH_APP_KEY);
        stringBuilder.append("=");
        stringBuilder.append(appKey);
        stringBuilder.append("&");
        stringBuilder.append("data");
        stringBuilder.append("=");
        stringBuilder.append(data);
        stringBuilder.append(secret);
        return stringBuilder.toString();
    }
}
