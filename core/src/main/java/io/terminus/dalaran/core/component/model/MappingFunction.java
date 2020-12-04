package io.terminus.dalaran.core.component.model;

import lombok.Data;

import java.util.Map;

@Data
public class MappingFunction {

    private String id;

    private String temKey;

    private FunctionType type;

    private Map<String, FunctionParam> params;

    private Map<String, FunctionParam> sourcePaths;
}
