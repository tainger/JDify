package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicClientInfo {

    private Long id;

    private Long moduleId;

    private String name;

    public BasicClientInfo() {
    }

    public BasicClientInfo(Long id, Long moduleId, String name) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
    }
}
