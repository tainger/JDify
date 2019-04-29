package io.terminus.dalaran.console.model.dto;

import lombok.Data;

@Data
public class BasicConnectorInfo {
    private Long id;

    private Long moduleId;

    private String name;

    public BasicConnectorInfo() {
    }

    public BasicConnectorInfo(Long id, Long moduleId, String name) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
    }
}
