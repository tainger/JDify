package io.terminus.dalaran.console.model.api;

import io.terminus.dalaran.component.common.HttpMethod;
import lombok.Data;

@Data
public class ApiInfo {
    private int paramLevel;
    private String name;
    private String moduleName;
    private String description;
    private String path;
    private HttpMethod method;
    private ApiParameter input;
    private ApiParameter output;
}
