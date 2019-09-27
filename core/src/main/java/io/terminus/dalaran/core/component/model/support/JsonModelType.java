package io.terminus.dalaran.core.component.model.support;

import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.schema.JsonSchema;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

@ModelType(value = "JSON", modelSchema = JsonSchema.class)
public class JsonModelType implements DalaranModelType<String, JsonSchema> {

    @Override
    public void fromObject(ProcessorDefinition route, JsonSchema schema) {
        route.marshal().json(JsonLibrary.Fastjson);
    }

    @Override
    public void toObject(ProcessorDefinition route, JsonSchema schema) {
        route.unmarshal().json(JsonLibrary.Fastjson);
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
