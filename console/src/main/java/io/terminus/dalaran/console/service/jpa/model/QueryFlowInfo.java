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

    private boolean online;

    public QueryFlowInfo() {
    }

    public QueryFlowInfo(String resourceKey, String moduleId, String name, FlowStatus status) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.status = status;
    }

    public QueryFlowInfo(String resourceKey, String moduleId, String name, FlowStatus status, String triggerType) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.status = status;
        this.triggerType = triggerType;
    }
}
