package io.terminus.dalaran.component.trigger.http;

import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.common.HttpProtocol;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import lombok.Data;

@Data
public class NettyHttpConfig extends AllModelConfig {

    // TODO load by application.yml
    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Hidden)
    private Integer port = 8080;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "HTTP")
    private HttpProtocol protocol;

    @ConfigFieldInfo(label = "方法", inputType = FieldInputType.Select, defaultValue = "GET")
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String, defaultValue = "/")
    private String path;

    @ConfigFieldInfo(label = "超时时间(ms)", inputType = FieldInputType.Integer, defaultValue = "3000")
    private Long timeout = 3000L;
}
