package io.terminus.dalaran.console.service.jpa.model;

import io.terminus.dalaran.model.function.MappingFunctionType;
import lombok.Data;

import java.util.List;

@Data
public class QueryFunctionInfo {

    private String resourceKey;

    private String moduleId;

    private String name;

    private String description;

    private MappingFunctionType type;

    private List<String> params;

    private boolean isExist;

    public QueryFunctionInfo() {
    }

    public QueryFunctionInfo(String resourceKey, String moduleId, String name, String description, MappingFunctionType type, List<String> params, boolean isExist) {
        this.resourceKey = resourceKey;
        this.moduleId = moduleId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.params = params;
        this.isExist = isExist;
    }
}
