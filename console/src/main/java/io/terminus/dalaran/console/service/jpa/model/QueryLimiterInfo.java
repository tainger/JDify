package io.terminus.dalaran.console.service.jpa.model;

import lombok.Data;

@Data
public class QueryLimiterInfo {

    private String resourceKey;

    private String moduleId;

    private String name;

    private String limiterType;

    public QueryLimiterInfo() {
    }

    public QueryLimiterInfo(String resourceKey, String moduleId, String name, String limiterType) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.limiterType = limiterType;
    }
}
