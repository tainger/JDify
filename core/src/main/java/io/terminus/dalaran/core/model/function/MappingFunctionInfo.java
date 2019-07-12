package io.terminus.dalaran.core.model.function;

import lombok.Data;

@Data
public class MappingFunctionInfo {

    private String key;
    private String description;

    private String[] params;
}
