package io.terminus.dalaran.support.convert;

import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranConverter;
import io.terminus.dalaran.DalaranConverterContext;
import io.terminus.dalaran.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.ObjectSchema;
import io.terminus.dalaran.model.schema.XMLSchema;
import io.terminus.dalaran.support.convert.converter.JsonConverter;
import io.terminus.dalaran.support.convert.converter.XMLConverter;
import org.apache.camel.model.RouteDefinition;

import java.util.HashMap;
import java.util.Map;

public class DefaultDalaranConverterContext implements DalaranConverterContext {
    private final Map<BodyType, DalaranConverter> converterMapping;
    private final Map<BodyType, Class<? extends DalaranModelSchema>> converterSchemaMapping;

    public DefaultDalaranConverterContext() {
        converterMapping = new HashMap<>();
        converterSchemaMapping = new HashMap<>();
        // TODO 这个扩展面也很窄, 先写死吧...
        converterMapping.put(BodyType.JSON, new JsonConverter());
        converterSchemaMapping.put(BodyType.JSON, JsonSchema.class);

        converterMapping.put(BodyType.XML, new XMLConverter());
        converterSchemaMapping.put(BodyType.XML, XMLSchema.class);

        converterSchemaMapping.put(BodyType.OBJECT, ObjectSchema.class);
    }

    @Override
    public Class<? extends DalaranModelSchema> getSchemaType(BodyType modelType) {
        return converterSchemaMapping.get(modelType);
    }

    @Override
    public void fromObject(RouteDefinition route, MessageModel model) {
        converterMapping.get(model.getModelType()).fromObject(route, model.getModelSchema());
    }

    @Override
    public void toObject(RouteDefinition route, MessageModel model) {
        converterMapping.get(model.getModelType()).toObject(route, model.getModelSchema());
    }

    @Override
    public void fromObject(RouteDefinition route, BodyType modelType) {
        converterMapping.get(modelType).fromObject(route, null);
    }

    @Override
    public void toObject(RouteDefinition route, BodyType modelType) {
        converterMapping.get(modelType).toObject(route, null);
    }
}
