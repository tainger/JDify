package io.terminus.dalaran.component.processor.http;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.model.HttpProtocol;
import lombok.Data;

@Data
public class HttpClientConnector {
    @ConfigFieldInfo(label = "服务地址", inputType = FieldInputType.String)
    private String host;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio, defaultValue = "HTTP")
    private HttpProtocol protocol;

    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Integer, defaultValue = "80")
    private Integer port = 80;

    @ConfigFieldInfo(label = "超时时间", inputType = FieldInputType.Integer, defaultValue = "3000")
    private Long timeout = 3000L;
}
