package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicServiceInfo {

    private String id;

    private String moduleId;

    private String type;

    private String name;

    public BasicServiceInfo() {
    }

    public BasicServiceInfo(String id, String moduleId, String type, String name) {
        this.id = id;
        this.moduleId = moduleId;
        this.type = type;
        this.name = name;
    }
}
