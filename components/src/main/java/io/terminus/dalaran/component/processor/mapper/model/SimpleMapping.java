package io.terminus.dalaran.component.processor.mapper.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.terminus.dalaran.component.processor.mapper.model.convert.MapperValueDeserializer;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/8
 */
@Data
public class SimpleMapping {

    @JSONField(deserializeUsing = MapperValueDeserializer.class)
    private Object value;

    private MappingType mappingType = MappingType.MAPPING;

//    private MappingFunction function;
}
