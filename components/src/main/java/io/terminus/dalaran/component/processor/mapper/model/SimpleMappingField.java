package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;

/**
 * Created by jingdi on 2019/5/8
 */
@Data
public class SimpleMappingField {

    private String value;

    private MappingType mappingType = MappingType.MAPPING;
}
