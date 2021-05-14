package io.terminus.dalaran.console.service.jpa.model;

import io.terminus.dalaran.model.flow.FlowStatus;
import lombok.Data;

@Data
public class QueryFlowInfo {

    private String resourceKey;

    private String moduleId;

    private String name;

    private FlowStatus status;

    private String triggerType;

    private boolean isExist;

    private boolean isOnline;

    public QueryFlowInfo() {
    }

    public QueryFlowInfo(String moduleId, String name, FlowStatus status, boolean isExist, String resourceKey) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.status = status;
        this.isExist = isExist;
    }

    public QueryFlowInfo(String moduleId, String name, FlowStatus status, String triggerType, boolean isExist, boolean isOnline, String resourceKey) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.status = status;
        this.triggerType = triggerType;
        this.isExist = isExist;
        this.isOnline = isOnline;
    }
}
