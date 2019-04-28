package io.terminus.dalaran.component.trigger.http;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.ModelRequiredConfig;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class NettyHttpConfig extends ModelRequiredConfig {

    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Integer, defaultValue = "8080")
    private Integer port = 8080;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, isEnum = true)
    private HttpProtocol protocol;

    @ConfigFieldInfo(label = "方法", inputType = FieldInputType.Select, isEnum = true)
    private HttpMethod method;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String)
    private String path;

    @ConfigFieldInfo(label = "超时时间(ms)", inputType = FieldInputType.Integer)
    private Long timeout = 3000L;
}
