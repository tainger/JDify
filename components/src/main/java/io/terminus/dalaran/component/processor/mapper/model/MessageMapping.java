package io.terminus.dalaran.component.processor.mapper.model;

import io.terminus.dalaran.core.component.model.MappingFunction;
import io.terminus.dalaran.core.component.model.MappingType;
import io.terminus.dalaran.model.FieldType;
import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/7/16
 */
@Data
public class MessageMapping {

    private String path;

    private MappingType mappingType;

    private FieldType type;

    private boolean complex;

    private MappingStatus status;

    private MappingFunction function;

    private SimpleMappingField destinationField;

    private List<SourceField> sourceFields;
}
