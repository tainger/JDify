package io.terminus.dalaran.component.processor.sgm;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class SGMHttpClientConnector {

    @ConfigFieldInfo(label = "服务地址", inputType = FieldInputType.String)
    private String host;

    @ConfigFieldInfo(label = "超时时间(ms)", inputType = FieldInputType.Integer, defaultValue = "3000")
    private Long timeout = 3000L;

    @ConfigFieldInfo(label = "token过期时间(s)", inputType = FieldInputType.Integer, defaultValue = "7000")
    private Long tokenTimeout;

    @ConfigFieldInfo(label = "应用id", inputType = FieldInputType.String)
    private String appId;

    @ConfigFieldInfo(label = "用于获取token的密码", inputType = FieldInputType.String)
    private String secret;

    @ConfigFieldInfo(label = "系统令牌", inputType = FieldInputType.String)
    private String token;

    private String sno;
}
