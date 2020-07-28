package io.terminus.dalaran.component.trigger.rest;

import io.terminus.dalaran.FieldInputType;
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

    @ConfigFieldInfo(label = "鉴权签名", inputType = FieldInputType.Switch, defaultValue = "false")
    private boolean enableSign = false;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "HTTP")
    private HttpProtocol protocol;

    @ConfigFieldInfo(label = "方法", inputType = FieldInputType.Select, defaultValue = "POST")
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "超时时间(ms)", inputType = FieldInputType.Integer, defaultValue = "3000")
    private Long timeout = 3000L;
}
