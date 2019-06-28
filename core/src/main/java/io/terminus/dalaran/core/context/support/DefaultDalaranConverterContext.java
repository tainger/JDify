package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.model.BodyType;
import io.terminus.dalaran.core.model.DalaranConverter;
import io.terminus.dalaran.core.model.DalaranModelSchema;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.model.converter.JsonConverter;
import io.terminus.dalaran.core.model.converter.XMLConverter;
import io.terminus.dalaran.core.model.converter.soap.SoapConverter;
import io.terminus.dalaran.core.model.schema.JsonSchema;
import io.terminus.dalaran.core.model.schema.ObjectSchema;
import io.terminus.dalaran.core.model.schema.SoapSchema;
import io.terminus.dalaran.core.model.schema.XMLSchema;
import org.apache.camel.model.ProcessorDefinition;

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

        converterMapping.put(BodyType.SOAP, new SoapConverter());
        converterSchemaMapping.put(BodyType.SOAP, SoapSchema.class);

        converterSchemaMapping.put(BodyType.OBJECT, ObjectSchema.class);
    }

    @Override
    public Class<? extends DalaranModelSchema> getSchemaType(BodyType modelType) {
        return converterSchemaMapping.get(modelType);
    }

    @Override
    public void fromObject(ProcessorDefinition route, MessageModel model) {
        if (model != null) {
            converterMapping.get(model.getModelType()).fromObject(route, model.getModelSchema());
        }
    }

    @Override
    public void toObject(ProcessorDefinition route, MessageModel model) {
        if (model != null) {
            converterMapping.get(model.getModelType()).toObject(route, model.getModelSchema());
        }
    }

    @Override
    public void fromObject(ProcessorDefinition route, BodyType modelType) {
        converterMapping.get(modelType).fromObject(route, null);
    }

    @Override
    public void toObject(ProcessorDefinition route, BodyType modelType) {
        converterMapping.get(modelType).toObject(route, null);
    }
}
