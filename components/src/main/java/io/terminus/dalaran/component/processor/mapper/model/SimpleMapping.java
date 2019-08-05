package io.terminus.dalaran.component.processor.mapper.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.terminus.dalaran.component.processor.mapper.model.convert.MapperValueDeserializer;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/8
 */
@Data
public class SimpleMapping<T> {

    @JSONField(deserializeUsing = MapperValueDeserializer.class)
    private T value;

    private MappingType mappingType = MappingType.MAPPING;

//    private MappingFunction function;
}
