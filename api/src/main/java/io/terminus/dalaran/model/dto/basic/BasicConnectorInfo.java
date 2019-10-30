package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicConnectorInfo {
    private Long id;

    private Long moduleId;

    private String name;

    private String connectorType;

    public BasicConnectorInfo() {
    }

    public BasicConnectorInfo(Long id, Long moduleId, String name, String connectorType) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.connectorType = connectorType;
    }
}
