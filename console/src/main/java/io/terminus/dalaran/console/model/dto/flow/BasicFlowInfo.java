package io.terminus.dalaran.console.model.dto.flow;

import io.terminus.dalaran.model.flow.FlowStatus;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class BasicFlowInfo {
    @Nullable
    private Long id;

    private Long moduleId;

    private String name;

    private FlowStatus status;

    private String triggerType;

    public BasicFlowInfo() {
    }

    public BasicFlowInfo(@Nullable Long id, Long moduleId, String name, FlowStatus status) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.status = status;
    }

    public BasicFlowInfo(@Nullable Long id, Long moduleId, String name, FlowStatus status, String triggerType) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.status = status;
        this.triggerType = triggerType;
    }
}
