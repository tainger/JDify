package io.terminus.dalaran.model.dto.flow;

import lombok.Data;

@Data
public class FrontendFlowDTO extends TriggerFlowDTO {

    private FlowType flowType;

    public enum FlowType {
        TriggerFlow, SubFlow
    }
}
