package io.terminus.dalaran.impl.converter;

import io.terminus.dalaran.DalaranConverter;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.HashMap;
import java.util.Map;

public class XMLConverter implements DalaranConverter {

    @Override
    public void toObject(ProcessorDefinition route) {
        route.to("log:parser[xml -> object]?showAll=true&multiline=true");
        route.marshal().xmljson();
        route.unmarshal().json(JsonLibrary.Gson, Map.class);
    }

    @Override
    public void fromObject(ProcessorDefinition route) {
        Map<String, String> options = new HashMap<>();
        // TODO for test
        options.put("rootName", "Order");

        route.to("log:parser[object -> xml]?showAll=true&multiline=true");
        route.marshal().json(JsonLibrary.Gson);
        route.unmarshal().xmljson(options);
    }
}
