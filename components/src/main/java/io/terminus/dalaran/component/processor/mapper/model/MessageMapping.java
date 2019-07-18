package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/7/16
 */
@Data
public class MessageMapping {

    private String path;

    private MappingType mappingType;

    private boolean complex;

    private MappingFunction function;

    private SimpleMappingField destinationField;

    private List<SourceField> sourceFields;

    private SimpleMappingField sourceRoot;

    private SimpleMappingField rootField;
}
