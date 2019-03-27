package io.terminus.dalaran.component.http.client;

import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.config.FieldInputType;
import lombok.Data;

@Data
public class HttpClientConfig {
    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio)
    private HttpProtocol protocol;
    private String host;
    private Integer port = 80;
    private String path;
    private HttpMethod method;
    private Long timeout = 3000L;
}