package io.terminus.dalaran.console.model.config.processor;

import io.terminus.dalaran.component.http.client.HttpMethod;
import io.terminus.dalaran.component.http.client.HttpProtocol;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by jingdi on 2019/4/4
 */
@Data
public class HttpClientConfig extends ProcessorConfig {

    private HttpProtocol protocol;

    private String host;

    private String port;

    private String path;

    private HttpMethod method;
}
