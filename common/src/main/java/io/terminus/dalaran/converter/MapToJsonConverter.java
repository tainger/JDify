package io.terminus.dalaran.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.Map;

public class MapToJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        return gson.toJson(attribute);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        return gson.fromJson(dbData, type);
    }
}
