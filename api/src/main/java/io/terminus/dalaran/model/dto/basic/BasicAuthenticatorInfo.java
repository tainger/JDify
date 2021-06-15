package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

@Data
public class BasicAuthenticatorInfo {

    private String id;

    private String moduleId;

    private String name;

    private boolean isExist;

    private String authenticatorType;

    public BasicAuthenticatorInfo(){
    }

    public BasicAuthenticatorInfo(String id, String moduleId, String name, boolean isExist, String authenticatorType) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.isExist = isExist;
        this.authenticatorType = authenticatorType;
    }
}
