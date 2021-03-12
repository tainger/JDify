package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicLimiterInfo {

    private String id;

    private String moduleId;

    private String name;

    private String limiterType;

    private boolean isExist;

    public BasicLimiterInfo() {
    }

    public BasicLimiterInfo(String id, String moduleId, String name, String limiterType, boolean isExist) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.limiterType = limiterType;
        this.isExist = isExist;
    }
}
