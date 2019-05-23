package io.terminus.dalaran.service.swagger;

import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.config.ImmutableModelConfig;
import lombok.Data;

@Data
public class SwaggerOperationConfig extends ImmutableModelConfig {

    private String url;

    private String path;

    private HttpMethod method;
}
