package io.terminus.dalaran.core.component.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SimpleMapping<T> {

    @JSONField(deserializeUsing = MapperValueDeserializer.class)
    private T value;

    private MappingType mappingType = MappingType.MAPPING;
}
