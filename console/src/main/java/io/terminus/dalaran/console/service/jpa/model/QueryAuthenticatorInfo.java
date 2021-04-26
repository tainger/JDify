package io.terminus.dalaran.console.service.jpa.model;

import lombok.Data;

@Data
public class QueryAuthenticatorInfo {

    private String resourceKey;

    private String moduleId;

    private String name;

    private String type;

    private boolean isExist;

    public QueryAuthenticatorInfo() {

    }

    public QueryAuthenticatorInfo(String resourceKey, String moduleId, String name, String type, boolean isExist) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.type = type;
        this.isExist = isExist;
    }
}
