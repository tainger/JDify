package io.terminus.dalaran.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.MODEL_ROOT;

// TODO Schema 最好有版本, 做升级时比较好处理
@Data
public class DalaranModelSchema {

    private static final Map<String, Class<? extends DalaranModelSchema>> modelSchemaMapping = new HashMap<>();

    private Map<String, ModelField> fields = new HashMap<>();

    public void setRootField(ModelField rootField) {
        fields.put(MODEL_ROOT, rootField);
    }

    public static void addModelSchema(String modelTypeName, Class<? extends DalaranModelSchema> modelSchemaClass) {
        modelSchemaMapping.put(modelTypeName, modelSchemaClass);
    }

    public static Class<? extends DalaranModelSchema> getModelSchemaClass(String modelTypeName) {
        return modelSchemaMapping.get(modelTypeName);
    }
}
