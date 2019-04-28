package io.terminus.dalaran.console.model.dto.flow;

import lombok.Data;

import java.util.Map;

@Data
public class TriggerFlowDTO extends BasicFlowDTO {

    private String triggerType;

    private Map<String, Object> triggerConfig;

}
