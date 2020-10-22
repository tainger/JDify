package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicLimiterInfo {

    private Long id;

    private Long moduleId;

    private String name;

    private String limiterType;

    public BasicLimiterInfo() {
    }

    public BasicLimiterInfo(Long id, Long moduleId, String name, String limiterType) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.limiterType = limiterType;
    }
}
