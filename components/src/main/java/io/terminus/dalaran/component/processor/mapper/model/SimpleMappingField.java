package io.terminus.dalaran.component.processor.mapper.model;

import io.terminus.dalaran.core.model.FieldType;
import lombok.Data;

/**
 * Created by jingdi on 2019/7/17
 */
@Data
public class SimpleMappingField {

    private String name;

    private FieldType type;

    private FieldLocal local;

    private SimpleMappingField child;
}
