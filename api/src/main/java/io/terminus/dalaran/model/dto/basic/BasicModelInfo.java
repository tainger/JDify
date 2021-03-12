package io.terminus.dalaran.model.dto.basic;


import lombok.Data;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class BasicModelInfo {

    private String id;

    private String moduleId;

    private String name;

    private String modelType;

    private boolean isExist;

    public BasicModelInfo() {
    }

    public BasicModelInfo(String id, String moduleId, String name, String modelType, boolean isExist) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.modelType = modelType;
        this.isExist = isExist;
    }
}

