package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.converter.DalaranConverter;
import io.terminus.dalaran.core.converter.JsonConverter;
import io.terminus.dalaran.core.converter.XMLConverter;
import io.terminus.dalaran.core.converter.soap.SoapConverter;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.ObjectSchema;
import io.terminus.dalaran.model.schema.SoapSchema;
import io.terminus.dalaran.model.schema.XMLSchema;
import org.apache.camel.model.ProcessorDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public Class<? extends DalaranModelSchema> getSchemaType(@NotNull BodyType modelType) {
        return converterSchemaMapping.get(modelType);
    }

    @Override
    public void fromObject(@NotNull ProcessorDefinition route, @Nullable MessageModel model, @NotNull BodyType modelType) {
        if (model == null) {
            fromObject(route, modelType);
        } else {
            fromObject(route, model);
        }
    }

    @Override
    public void toObject(@NotNull ProcessorDefinition route, @Nullable MessageModel model, @NotNull BodyType modelType) {
        if (model == null) {
            toObject(route, modelType);
        } else {
            toObject(route, model);
        }
    }

    @Override
    public void fromObject(@NotNull ProcessorDefinition route, @NotNull MessageModel model) {
        converterMapping.get(model.getModelType()).fromObject(route, model.getModelSchema());
    }

    @Override
    public void toObject(@NotNull ProcessorDefinition route, @NotNull MessageModel model) {
        converterMapping.get(model.getModelType()).toObject(route, model.getModelSchema());
    }

    @Override
    public void fromObject(@NotNull ProcessorDefinition route, @NotNull BodyType modelType) {
        converterMapping.get(modelType).fromObject(route, null);
    }

    @Override
    public void toObject(@NotNull ProcessorDefinition route, @NotNull BodyType modelType) {
        converterMapping.get(modelType).toObject(route, null);
    }
}
