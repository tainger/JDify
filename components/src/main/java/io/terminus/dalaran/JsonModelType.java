package io.terminus.dalaran;

import io.terminus.dalaran.core.model.ModelType;
import io.terminus.dalaran.model.schema.JsonSchema;

public class JsonModelType implements ModelType<String, JsonSchema> {



    @Override
    public String fromObject(Object obj, JsonSchema schema) {
        return null;
    }

    @Override
    public Object toObject(String data, JsonSchema schema) {
        return null;
    }

    @Override
    public String buildTemplateData(JsonSchema schema) {
        return null;
    }

    @Override
    public JsonSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }
}
