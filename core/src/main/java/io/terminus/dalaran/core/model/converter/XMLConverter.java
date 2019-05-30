package io.terminus.dalaran.core.model.converter;

import io.terminus.dalaran.core.model.DalaranConverter;
import io.terminus.dalaran.core.model.schema.XMLSchema;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.HashMap;
import java.util.Map;

public class XMLConverter implements DalaranConverter<XMLSchema> {

    @Override
    public void toObject(ProcessorDefinition route, XMLSchema schema) {
        route.marshal().xmljson(buildOptions(schema));
        route.unmarshal().json(JsonLibrary.Fastjson);
    }

    @Override
    public void fromObject(ProcessorDefinition route, XMLSchema schema) {
        route.marshal().json(JsonLibrary.Fastjson);
        route.unmarshal().xmljson(buildOptions(schema));
    }

    // TODO 处理 XML 特殊逻辑, 比如 attr, 应该可以抽象
    private Map<String, String> buildOptions(XMLSchema schema) {
        Map<String, String> options = new HashMap<>();
        options.put("rootName", schema.getRoot());
        return options;
    }
}
