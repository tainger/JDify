package io.terminus.dalaran.console.service.jpa.model;


import lombok.Data;

@Data
public class QueryAlarmRuleInfo {

    private String resourceKey;

    private String moduleId;

    private String name;

    public QueryAlarmRuleInfo(String resourceKey, String moduleId, String name) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
    }
}
