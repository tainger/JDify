package io.terminus.dalaran.support.convert.converter;

import io.terminus.dalaran.DalaranConverter;
import io.terminus.dalaran.model.schema.JsonSchema;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Map;

public class JsonConverter implements DalaranConverter<JsonSchema> {

    @Override
    public void toObject(ProcessorDefinition route, JsonSchema schema) {
        route.unmarshal().json(JsonLibrary.Gson);
    }

    @Override
    public void fromObject(ProcessorDefinition route, JsonSchema schema) {
        route.marshal().json(JsonLibrary.Gson);
    }
}
