package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/7/18
 */
@Data
public class MappingFunction {

    private String id;

    private String temKey;

    private FunctionType type;

    private Map<String, FunctionParam> params;
}
