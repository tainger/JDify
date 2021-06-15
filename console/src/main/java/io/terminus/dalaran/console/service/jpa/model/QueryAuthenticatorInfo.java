package io.terminus.dalaran.console.service.jpa.model;

import lombok.Data;

@Data
public class QueryAuthenticatorInfo {

    private String resourceKey;

    private String moduleId;

    private String name;

    private String authenticatorType;

    private boolean isExist;

    public QueryAuthenticatorInfo() {

    }

    public QueryAuthenticatorInfo(String resourceKey, String moduleId, String name, String authenticatorType, boolean isExist) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.authenticatorType = authenticatorType;
        this.isExist = isExist;
    }
}
