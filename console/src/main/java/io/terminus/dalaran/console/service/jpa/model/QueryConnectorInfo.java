package io.terminus.dalaran.console.service.jpa.model;

import lombok.Data;

@Data
public class QueryConnectorInfo {

    private String resourceKey;

    private String moduleId;

    private String name;

    private String connectorType;

    public QueryConnectorInfo() {
    }

    public QueryConnectorInfo(String resourceKey, String moduleId, String name, String connectorType) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.connectorType = connectorType;
    }
}
