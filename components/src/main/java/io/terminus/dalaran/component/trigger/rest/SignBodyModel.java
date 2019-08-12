package io.terminus.dalaran.component.trigger.rest;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.hash.Hashing;

import static io.terminus.dalaran.DalaranConstants.AUTH_APP_KEY;

@Data
public class SignBodyModel {

    private String appKey;

    private Object data;

    private String sign;

    boolean checkSign(String secret) {
        if (StringUtils.isEmpty(secret)) {
            return false;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(AUTH_APP_KEY);
        stringBuilder.append("=");
        stringBuilder.append(appKey);
        stringBuilder.append("&");
        stringBuilder.append("data");
        stringBuilder.append("=");
        stringBuilder.append(JSON.toJSONString(data));
        stringBuilder.append("=");
        stringBuilder.append(secret);
        String backendSign = Hashing.md5().hashString(stringBuilder.toString(), Charsets.UTF_8).toString();
        return StringUtils.equalsIgnoreCase(sign, backendSign);
    }
}
