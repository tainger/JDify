package io.terminus.dalaran.component.processor.sgm;

import io.terminus.dalaran.component.processor.http.HttpClientConnector;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class SGMHttpClientConnector extends HttpClientConnector {

    @ConfigFieldInfo(label = "token 过期时间", inputType = FieldInputType.Integer, defaultValue = "7000")
    private Long tokenTimeout;

    @ConfigFieldInfo(label = "token 获取地址", inputType = FieldInputType.String)
    private String authUrl;

    @ConfigFieldInfo(label = "应用id", inputType = FieldInputType.String)
    private String appId;

    @ConfigFieldInfo(label = "用于获取token的密码", inputType = FieldInputType.String)
    private String secret;

    @ConfigFieldInfo(label = "系统令牌", inputType = FieldInputType.String)
    private String token;

    private String sno;
}
