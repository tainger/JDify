package io.terminus.dalaran.component.http.client;

import io.terminus.dalaran.annotation.DalaranConfigField;
import io.terminus.dalaran.config.FieldInputType;
import lombok.Data;

@Data
public class HttpClientConfig {
    @DalaranConfigField(label = "协议", inputType = FieldInputType.Radio)
    private HttpProtocol protocol;
    private String host;
    private Integer port;
    private String path;
    private HttpMethod method;
}