package io.terminus.dalaran.support.convert;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.DalaranConverter;
import io.terminus.dalaran.DalaranConverterContext;
import io.terminus.dalaran.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.XMLSchema;
import io.terminus.dalaran.support.convert.converter.JsonConverter;
import io.terminus.dalaran.support.convert.converter.XMLConverter;
import org.apache.camel.model.RouteDefinition;

import java.util.HashMap;
import java.util.Map;

public class DefaultDalaranConverterContext implements DalaranConverterContext {
    private final Map<BodyModelType, DalaranConverter> converterMapping;
    private final Map<BodyModelType, Class<? extends DalaranModelSchema>> converterSchemaMapping;

    public DefaultDalaranConverterContext() {
        converterMapping = new HashMap<>();
        converterSchemaMapping = new HashMap<>();
        // TODO 这个扩展面也很窄, 先写死吧...
        converterMapping.put(BodyModelType.JSON, new JsonConverter());
        converterSchemaMapping.put(BodyModelType.JSON, JsonSchema.class);
        converterMapping.put(BodyModelType.XML, new XMLConverter());
        converterSchemaMapping.put(BodyModelType.XML, XMLSchema.class);
    }

    @Override
    public Class<? extends DalaranModelSchema> getSchemaType(BodyModelType modelType) {
        return converterSchemaMapping.get(modelType);
    }

    @Override
    public void unmarshal(RouteDefinition route, MessageModel model) {
        converterMapping.get(model.getModelType()).fromObject(route, model.getModelSchema());
    }

    @Override
    public void marshal(RouteDefinition route, MessageModel model) {
        converterMapping.get(model.getModelType()).toObject(route, model.getModelSchema());
    }
}
