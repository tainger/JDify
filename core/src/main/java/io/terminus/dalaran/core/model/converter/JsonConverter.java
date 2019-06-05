package io.terminus.dalaran.core.model.converter;

import io.terminus.dalaran.core.model.DalaranConverter;
import io.terminus.dalaran.core.model.schema.JsonSchema;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

public class JsonConverter implements DalaranConverter<JsonSchema> {

    @Override
    public void toObject(ProcessorDefinition route, JsonSchema schema) {
        route.unmarshal().json(JsonLibrary.Fastjson);
    }

    @Override
    public void fromObject(ProcessorDefinition route, JsonSchema schema) {
        route.marshal().json(JsonLibrary.Fastjson);
    }
}
