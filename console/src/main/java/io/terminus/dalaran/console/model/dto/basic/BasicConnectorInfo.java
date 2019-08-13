package io.terminus.dalaran.console.model.dto.basic;

import io.terminus.dalaran.core.component.ComponentType;
import lombok.Data;

@Data
public class BasicConnectorInfo {
    private Long id;

    private Long moduleId;

    private String name;

    private ComponentType componentType;

    private String componentName;

    public BasicConnectorInfo() {
    }

    public BasicConnectorInfo(Long id, Long moduleId, String name, ComponentType componentType, String componentName) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.componentType = componentType;
        this.componentName = componentName;
    }
}
