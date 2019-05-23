package io.terminus.dalaran.service.swagger;

import lombok.Data;

import java.util.List;

@Data
public class SwaggerServiceConfig {

    private String url;

    private String basePath;

    private List<SwaggerOperationConfig> configs;
}
