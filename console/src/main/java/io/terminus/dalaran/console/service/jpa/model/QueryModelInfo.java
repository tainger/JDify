package io.terminus.dalaran.console.service.jpa.model;

import lombok.Data;

@Data
public class QueryModelInfo {

    private String resourceKey;

    private String moduleId;

    private String name;

    private String modelType;

    public QueryModelInfo() {
    }

    public QueryModelInfo(String resourceKey, String moduleId, String name, String modelType) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.modelType = modelType;
    }
}
