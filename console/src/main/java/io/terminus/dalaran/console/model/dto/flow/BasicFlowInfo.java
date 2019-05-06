package io.terminus.dalaran.console.model.dto.flow;

import io.terminus.dalaran.FlowStatus;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class BasicFlowInfo {
    @Nullable
    private Long id;

    private Long moduleId;

    private String name;

    private FlowStatus status;

    public BasicFlowInfo() {
    }

    public BasicFlowInfo(@Nullable Long id, Long moduleId, String name, FlowStatus status) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.status = status;
    }
}
