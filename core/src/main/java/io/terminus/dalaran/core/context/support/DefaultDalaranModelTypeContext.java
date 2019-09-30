package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import org.apache.camel.model.ProcessorDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultDalaranModelTypeContext implements DalaranModelTypeContext {
    private static final Map<String, DalaranModelType> modelTypeMapping = new HashMap<>();

    @Override
    public void fromObject(@NotNull ProcessorDefinition route, @Nullable MessageModel model, @NotNull String modelType) {
        if (model == null) {
            fromObject(route, modelType);
        } else {
            fromObject(route, model);
        }
    }

    @Override
    public void toObject(@NotNull ProcessorDefinition route, @Nullable MessageModel model, @NotNull String modelType) {
        if (model == null) {
            toObject(route, modelType);
        } else {
            toObject(route, model);
        }
    }

    @Override
    public void fromObject(@NotNull ProcessorDefinition route, @NotNull MessageModel model) {
        modelTypeMapping.get(model.getModelType()).fromObject(route, model.getModelSchema());
    }

    @Override
    public void toObject(@NotNull ProcessorDefinition route, @NotNull MessageModel model) {
        modelTypeMapping.get(model.getModelType()).toObject(route, model.getModelSchema());
    }

    @Override
    public void fromObject(@NotNull ProcessorDefinition route, @NotNull String modelType) {
        modelTypeMapping.get(modelType).fromObject(route, null);
    }

    @Override
    public void toObject(@NotNull ProcessorDefinition route, @NotNull String modelType) {
        modelTypeMapping.get(modelType).toObject(route, null);
    }

    @Override
    public void addModelType(String modelTypeName, Class<? extends DalaranModelSchema> schemaType, DalaranModelType modelType) {
        modelTypeMapping.put(modelTypeName, modelType);
        DalaranModelSchema.addModelSchema(modelTypeName, schemaType);
    }

    @Override
    public Set<String> listAllModelType() {
        return modelTypeMapping.keySet();
    }

    @Override
    public DalaranModelType getModelType(String modelTypeName) {
        return modelTypeMapping.get(modelTypeName);
    }

    @Override
    public Class<? extends DalaranModelSchema> getModelSchema(String modelTypeName) {
        return DalaranModelSchema.getModelSchemaClass(modelTypeName);
    }

}
