package io.terminus.dalaran.component.processor.http;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.ModelRequiredConfig;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class HttpClientConfig extends ModelRequiredConfig {

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, isEnum = true)
    private HttpProtocol protocol;

    @ConfigFieldInfo(label = "服务地址", inputType = FieldInputType.String)
    private String host;

    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Integer, defaultValue = "80")
    private Integer port = 80;

    @ConfigFieldInfo(label = "路径", inputType = FieldInputType.String)
    private String path;

    @ConfigFieldInfo(label = "类型", inputType = FieldInputType.Select, isEnum = true)
    private HttpMethod method;

    @ConfigFieldInfo(label = "超时时间", inputType = FieldInputType.Integer, defaultValue = "3000L")
    private Long timeout = 3000L;
}