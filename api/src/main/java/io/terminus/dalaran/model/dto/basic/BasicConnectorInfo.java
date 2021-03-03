package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicConnectorInfo {

    private String id;

    private String moduleId;

    private String name;

    private String connectorType;

    public BasicConnectorInfo() {
    }

    public BasicConnectorInfo(String id, String moduleId, String name, String connectorType) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.connectorType = connectorType;
    }
}
