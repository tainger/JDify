package io.terminus.dalaran.component.trigger.rest;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.common.ContentType;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import io.terminus.dalaran.model.HttpProtocol;
import lombok.Data;

@Data
public class RestConfig extends AllModelConfig {

    // TODO load by application.yml
    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Hidden)
    private Integer port = 8080;

    @ConfigFieldInfo(label = "开启鉴权", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean enableSign = false;

    @ConfigFieldInfo(label = "计算签名", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean checkSign = false;

    @ConfigFieldInfo(label = "密钥", inputType = FieldInputType.Password, required = false)
    private String secret;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "HTTP")
    private HttpProtocol protocol;

    @ConfigFieldInfo(label = "方法", inputType = FieldInputType.Select, defaultValue = "POST")
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "数据类型", inputType = FieldInputType.Select, defaultValue = "APPLICATION_JSON")
    private ContentType contentType = ContentType.APPLICATION_JSON;

    @ConfigFieldInfo(label = "超时时间(ms)", inputType = FieldInputType.Integer, defaultValue = "3000")
    private Long timeout = 3000L;
}
