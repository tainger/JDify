package io.terminus.dalaran.core.model;

import io.terminus.dalaran.model.DalaranModelSchema;

public interface ModelType<T, Schema extends DalaranModelSchema> {

    T fromObject(Object obj, Schema schema);

    Object toObject(T data, Schema schema);

    String buildTemplateData(Schema schema);

    Schema buildSchemaFromTemplateData(String dataStr);

}
