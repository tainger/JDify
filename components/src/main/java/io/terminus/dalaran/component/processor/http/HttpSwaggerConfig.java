package io.terminus.dalaran.component.processor.http;

import lombok.Data;

@Data
public class HttpSwaggerConfig {

    private String swaggerUrl;

    private String path;

    private String method;
}
