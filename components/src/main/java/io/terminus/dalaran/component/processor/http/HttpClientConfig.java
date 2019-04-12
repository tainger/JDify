package io.terminus.dalaran.component.processor.http;

import io.terminus.dalaran.ModelRequiredConfig;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class HttpClientConfig extends ModelRequiredConfig {
    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio)
    private HttpProtocol protocol;
    private String host;
    private Integer port = 80;
    private String path;
    private HttpMethod method;
    private Long timeout = 3000L;
}