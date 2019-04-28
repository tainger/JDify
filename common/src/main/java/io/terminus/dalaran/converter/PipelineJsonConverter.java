package io.terminus.dalaran.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.terminus.dalaran.entity.ProcessorEntity;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class PipelineJsonConverter implements AttributeConverter<List<ProcessorEntity>, String> {

    private final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(List attribute) {
        return gson.toJson(attribute);
    }

    @Override
    public List<ProcessorEntity> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<ProcessorEntity>>() {
        }.getType();
        return gson.fromJson(dbData, type);
    }
}

