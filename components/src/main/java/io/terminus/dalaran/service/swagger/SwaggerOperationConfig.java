package io.terminus.dalaran.service.swagger;

import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.config.AllModelConfig;
import lombok.Data;

@Data
public class SwaggerOperationConfig extends AllModelConfig {

    private String path;

    private HttpMethod method;

}
