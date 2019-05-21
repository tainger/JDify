package io.terminus.dalaran.component.processor.swagger;

import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.config.ServiceProcessorConfig;
import lombok.Data;

@Data
public class SwaggerServiceClientConfig extends ServiceProcessorConfig {

    private String path;

    private HttpMethod method;
}
