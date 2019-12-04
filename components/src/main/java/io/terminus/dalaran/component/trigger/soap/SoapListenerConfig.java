package io.terminus.dalaran.component.trigger.soap;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.trigger.soap.model.SoapAuthType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import io.terminus.dalaran.model.HttpProtocol;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/13
 */
@Data
public class SoapListenerConfig extends AllModelConfig {

    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Hidden)
    private Integer port = 8080;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "HTTP")
    private HttpProtocol protocol;

    @ConfigFieldInfo(label = "方法", inputType = FieldInputType.Select, defaultValue = "POST")
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "超时时间(ms)", inputType = FieldInputType.Integer, defaultValue = "3000")
    private Long timeout = 3000L;

    @ConfigFieldInfo(label = "鉴权签名", inputType = FieldInputType.Switch, defaultValue = "true")
    private boolean enableSign = false;

    @ConfigFieldInfo(label = "鉴权类型", inputType = FieldInputType.Select, required = false)
    private SoapAuthType authType;

    @ConfigFieldInfo(label = "返回体为空", inputType = FieldInputType.Switch, required = false)
    private boolean nullResponseBody = false;
}
