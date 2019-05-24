package io.terminus.dalaran.service.swagger;

import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.model.ServiceOperation;
import lombok.Data;

@Data
public class SwaggerOperationConfig extends ServiceOperation {

    private String url;

    private String path;

    private HttpMethod method;
}
