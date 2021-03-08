package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicLimiterInfo {

    private String id;

    private String moduleId;

    private String name;

    private String limiterType;

    public BasicLimiterInfo() {
    }

    public BasicLimiterInfo(String id, String moduleId, String name, String limiterType) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.limiterType = limiterType;
    }
}
