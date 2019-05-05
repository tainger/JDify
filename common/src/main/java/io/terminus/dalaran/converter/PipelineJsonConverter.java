package io.terminus.dalaran.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.terminus.dalaran.entity.ProcessorEntity;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class PipelineJsonConverter implements AttributeConverter<List<ProcessorEntity>, String> {

    @Override
    public String convertToDatabaseColumn(List attribute) {
        return JSON.toJSONString(attribute);
    }

    @Override
    public List<ProcessorEntity> convertToEntityAttribute(String dbData) {
        Type type = new TypeReference<List<ProcessorEntity>>() {
        }.getType();
        return JSON.parseObject(dbData, type);
    }
}

