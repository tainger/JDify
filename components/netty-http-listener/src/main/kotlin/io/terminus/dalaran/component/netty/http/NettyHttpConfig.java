package io.terminus.dalaran.component.netty.http;

import io.terminus.dalaran.annotation.DalaranConfigField;
import io.terminus.dalaran.config.FieldInputType;
import lombok.Data;

@Data
public class NettyHttpConfig {
    @DalaranConfigField(label = "协议", inputType = FieldInputType.Radio)
    private HttpProtocol protocol;
    private String host;
    private String port;
    private String path;
    private HttpMethod method;
}
