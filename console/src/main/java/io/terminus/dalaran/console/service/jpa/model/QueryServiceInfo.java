package io.terminus.dalaran.console.service.jpa.model;

import lombok.Data;

@Data
public class QueryServiceInfo {

    private String resourceKey;

    private String moduleId;

    private String type;

    private String name;

    public QueryServiceInfo() {
    }

    public QueryServiceInfo(String resourceKey, String moduleId, String type, String name) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.type = type;
        this.name = name;
    }
}
