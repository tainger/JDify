package io.terminus.dalaran.console.service.jpa.model;

import lombok.Data;

@Data
public class QueryAuthenticatorInfo {

    private String resourceKey;

    private String moduleId;

    private String name;

    private boolean isExist;

    public QueryAuthenticatorInfo() {

    }

    public QueryAuthenticatorInfo(String resourceKey, String moduleId, String name, boolean isExist) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.isExist = isExist;
    }
}
