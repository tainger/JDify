package io.terminus.dalaran.component.processor.mapper.model;

import io.terminus.dalaran.FieldType;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/4/22
 */
@Data
public class MappingField {

    private FieldType type;

    private FieldType subType;

    private MappingFieldType mappingType;

    private String value;

    private FieldType mappingFieldType;

    private Map<String, MappingField> mapping;
}
