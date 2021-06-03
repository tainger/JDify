package io.terminus.dalaran.console.service.jpa.model;

import lombok.Data;

@Data
public class QueryServiceInfo {

    private String resourceKey;

    private String nodeId;

    private String moduleId;

    private String type;

    private String name;

    private boolean isExist;

    public QueryServiceInfo() {
    }

    public QueryServiceInfo(String resourceKey, String nodeId, String moduleId, String type, String name, boolean isExist) {
        this.resourceKey = resourceKey;
        this.nodeId = nodeId;
        this.moduleId = moduleId;
        this.type = type;
        this.name = name;
        this.isExist = isExist;
    }
}
