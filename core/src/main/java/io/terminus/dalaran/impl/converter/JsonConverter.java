package io.terminus.dalaran.impl.converter;

import io.terminus.dalaran.DalaranConverter;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Map;

public class JsonConverter implements DalaranConverter {

    @Override
    public void toObject(ProcessorDefinition route) {
        route.unmarshal().json(JsonLibrary.Gson, Map.class);
    }

    @Override
    public void fromObject(ProcessorDefinition route) {
        route.marshal().json(JsonLibrary.Gson);
    }
}
