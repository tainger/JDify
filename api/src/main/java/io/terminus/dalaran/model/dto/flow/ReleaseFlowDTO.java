package io.terminus.dalaran.model.dto.flow;

import lombok.Data;

import java.util.Map;

@Data
public class ReleaseFlowDTO extends BasicFlowDTO {

    private String triggerType;

    private boolean tracing;

    private Map<String, Object> triggerConfig;

    private String moduleName;

    private String createdAt;

    private String updatedAt;

}
