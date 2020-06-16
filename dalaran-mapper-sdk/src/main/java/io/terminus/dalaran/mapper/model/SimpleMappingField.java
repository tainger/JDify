package io.terminus.dalaran.mapper.model;

import io.terminus.dalaran.model.FieldType;
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
