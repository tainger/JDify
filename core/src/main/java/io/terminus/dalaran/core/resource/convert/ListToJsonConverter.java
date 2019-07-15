package io.terminus.dalaran.core.resource.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class ListToJsonConverter implements AttributeConverter<List, String> {

    @Override
    public String convertToDatabaseColumn(List attribute) {
        return JSON.toJSONString(attribute);
    }

    @Override
    public List convertToEntityAttribute(String dbData) {
        Type type = new TypeReference<List>() {
        }.getType();
        return JSON.parseObject(dbData, type);
    }
}

