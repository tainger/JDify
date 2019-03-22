package io.terminus.dalaran.support.component.netty.http;

import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.config.FieldInputType;
import lombok.Data;

@Data
public class NettyHttpConfig {
    @ConfigFieldInfo(label = "协议", inputType = FieldInputType.Radio)
    private HttpProtocol protocol;
    private String host;
    private Integer port;
    private String path;
    private HttpMethod method;
}
