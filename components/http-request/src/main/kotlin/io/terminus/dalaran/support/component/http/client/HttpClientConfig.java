package io.terminus.dalaran.support.component.http.client;

import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.config.FieldInputType;
import lombok.Data;

@Data
public class HttpClientConfig {
    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio)
    private HttpProtocol protocol;
    private String host;
    private Integer port;
    private String path;
    private HttpMethod method;
}