package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicClientInfo {

    private String id;

    private String moduleId;

    private String name;

    private boolean isExist;

    public BasicClientInfo() {
    }

    public BasicClientInfo(String id, String moduleId, String name, boolean isExist) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.isExist = isExist;
    }
}
