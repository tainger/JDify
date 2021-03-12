package io.terminus.dalaran.model.dto.basic;

import io.terminus.dalaran.model.function.MappingFunctionType;
import lombok.Data;

import java.util.List;

@Data
public class BasicFunctionInfo {

    private String id;

    private String moduleId;

    private String name;

    private String description;

    private MappingFunctionType type;

    private List<String> params;

    private boolean isExist;

    public BasicFunctionInfo() {
    }

    public BasicFunctionInfo(String id, String moduleId, String name, String description, MappingFunctionType type, List<String> params, boolean isExist) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.params = params;
        this.isExist = isExist;
    }
}
