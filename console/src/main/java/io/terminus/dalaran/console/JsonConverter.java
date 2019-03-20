package io.terminus.dalaran.console;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class JsonConverter implements AttributeConverter<List, String> {

    private final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(List attribute) {
        return gson.toJson(attribute);
    }

    @Override
    public List convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<Long>>(){}.getType();
        return gson.fromJson(dbData, type);
    }
}
