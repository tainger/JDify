package io.terminus.dalaran.console.model.dto;

import io.terminus.dalaran.model.function.MappingFunctionType;
import lombok.Data;

import java.util.List;

@Data
public class BasicFunctionInfo {

    private Long id;
    private Long moduleId;
    private String name;
    private String description;
    private MappingFunctionType type;
    private List<String> params;

    public BasicFunctionInfo() {
    }

    public BasicFunctionInfo(Long id, Long moduleId, String name, String description, MappingFunctionType type, List<String> params) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.params = params;
    }
}
