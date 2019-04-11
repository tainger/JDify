package io.terminus.dalaran.console.model.config.trigger;

import io.terminus.dalaran.component.netty.http.HttpMethod;
import io.terminus.dalaran.component.netty.http.HttpProtocol;
import lombok.Data;

/**
 * Created by jingdi on 2019/4/4
 */
@Data
public class RestApiConfig {

    private HttpProtocol protocol;

    private String host;

    private String port;

    private String path;

    private HttpMethod method;
}
