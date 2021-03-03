package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicClientInfo {

    private String id;

    private String moduleId;

    private String name;

    public BasicClientInfo() {
    }

    public BasicClientInfo(String id, String moduleId, String name) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
    }
}
