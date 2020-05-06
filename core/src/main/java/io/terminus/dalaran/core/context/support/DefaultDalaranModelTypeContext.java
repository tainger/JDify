package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.config.DalaranConfigField;
import io.terminus.dalaran.config.ModelInfo;
import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import io.terminus.dalaran.core.util.I18nUtils;
import io.terminus.dalaran.core.util.ModelFieldUtils;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import org.apache.camel.model.ProcessorDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultDalaranModelTypeContext implements DalaranModelTypeContext {

    @Autowired
    private I18nUtils i18nUtils;

    private static final Map<String, DalaranModelType> modelTypeMapping = new HashMap<>();

    private final Map<String, ModelInfo> modelInfoMapping = new ConcurrentHashMap<>();

    @Override
    public void fromObject(@NotNull ProcessorDefinition route, @Nullable MessageModel model, @NotNull String modelType) {
        if (model == null) {
            fromObject(route, modelType);
        } else if (model.getModelType().equals(modelType) || model.getModelType().equals("SOAP")) {
            // TODO 这种要考虑一下风险, 比如 SOAP 这种有特殊要求的 Schema
            fromObject(route, model);
        } else {
            fromObject(route, modelType);
        }
    }

    @Override
    public void toObject(@NotNull ProcessorDefinition route, @Nullable MessageModel model, @NotNull String modelType) {
        if (model == null) {
            toObject(route, modelType);
        } else if (model.getModelType().equals(modelType)) {
            toObject(route, model);
        } else {
            // TODO 这种要考虑一下风险, 比如 SOAP 这种有特殊要求的 Schema
            toObject(route, modelType);
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
    public void addModelType(DalaranModelType modelType) {
        ModelType modelTypeAnnotation = modelType.getClass().getAnnotation(ModelType.class);
        DalaranConfigField[] configFields = ModelFieldUtils.buildModelFields(modelTypeAnnotation.modelSchema());
        String modelTypeInfo = modelTypeAnnotation.value();
        ModelInfo modelInfo = new ModelInfo();
        modelInfo.setType(modelTypeInfo);
        modelInfo.setName(modelTypeInfo);
        modelInfo.setConfigFields(configFields);
        modelInfoMapping.put(modelTypeInfo, modelInfo);
        modelTypeMapping.put(modelTypeInfo, modelType);
        DalaranModelSchema.addModelSchema(modelTypeInfo, modelTypeAnnotation.modelSchema());
    }

    @Override
    public Collection<ModelInfo> listAllModelInfo() {
        return modelInfoMapping.values().stream().map(modelInfo -> {
            ModelInfo newModelInfo = new ModelInfo();
            BeanUtils.copyProperties(modelInfo, newModelInfo);
            newModelInfo.setName(modelInfo.getType());
            List<DalaranConfigField> fields = new ArrayList<>();
            for (DalaranConfigField configField : newModelInfo.getConfigFields()) {
                DalaranConfigField i18nField = new DalaranConfigField();
                fields.add(i18nField);
                BeanUtils.copyProperties(configField, i18nField);
                i18nField.setLabel(i18nUtils.getModelFieldLabel(newModelInfo.getType(), configField.getName()));
            }
            newModelInfo.setConfigFields(fields.toArray(new DalaranConfigField[0]));
            return newModelInfo;
        }).collect(Collectors.toList());
    }

    @Override
    public ModelInfo getModelInfo(String modelType) {
        return modelInfoMapping.get(modelType);
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
