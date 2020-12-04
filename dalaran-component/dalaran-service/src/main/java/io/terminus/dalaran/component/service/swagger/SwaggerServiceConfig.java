package io.terminus.dalaran.component.service.swagger;

import lombok.Data;

import java.util.List;

@Data
public class SwaggerServiceConfig {

    private String url;

    private String basePath;

    private List<SwaggerOperationConfig> configs;
}
