package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicConnectorInfo {

    private String id;

    private String nodeId;

    private String moduleId;

    private String name;

    private String connectorType;

    private boolean isExist;

    public BasicConnectorInfo() {
    }

    public BasicConnectorInfo(String id, String nodeId, String moduleId, String name, String connectorType, boolean isExist) {
        this.id = id;
        this.nodeId = nodeId;
        this.moduleId = moduleId;
        this.name = name;
        this.connectorType = connectorType;
        this.isExist = isExist;
    }
}
