package io.terminus.dalaran.model.dto.flow;

import lombok.Data;

import java.util.Map;

@Data
public class TriggerFlowDTO extends BasicFlowDTO {

    private String triggerType;

    private boolean tracing;

    private Map<String, Object> triggerConfig;

}
