package io.terminus.dalaran.component.netty.http;

import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.config.FieldInputType;
import lombok.Data;

@Data
public class NettyHttpConfig {
    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    private String host = "localhost";

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    // TODO load server.port param...
    private Integer port = 8080;

    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio)
    private HttpProtocol protocol;

    private String path;

    private HttpMethod method;

    private Long timeout = 3000L;
}
