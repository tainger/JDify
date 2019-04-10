package io.terminus.dalaran.support.jpa;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class ListToJsonConverter implements AttributeConverter<List, String> {

    private final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(List attribute) {
        return gson.toJson(attribute);
    }

    @Override
    public List convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<Long>>() {
        }.getType();
        return gson.fromJson(dbData, type);
    }
}

