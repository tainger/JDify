package io.terminus.dalaran.model.dto.basic;

import io.terminus.dalaran.model.flow.FlowStatus;
import lombok.Data;

@Data
public class BasicFlowInfo {

    private String id;

    private String moduleId;

    private String name;

    private FlowStatus status;

    private String triggerType;

    private boolean isExist;

    private boolean isOnline;

    private String resourceKey;

    public BasicFlowInfo() {
    }

    public BasicFlowInfo(String id, String moduleId, String name, FlowStatus status, String triggerType, boolean isExist, boolean isOnline) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.status = status;
        this.triggerType = triggerType;
        this.isExist = isExist;
        this.isOnline = isOnline;
    }

    public BasicFlowInfo(String id, String moduleId, String name, FlowStatus status, String triggerType) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.status = status;
        this.triggerType = triggerType;
    }
}
