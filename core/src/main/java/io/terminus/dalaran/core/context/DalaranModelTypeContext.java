package io.terminus.dalaran.core.context;

import io.terminus.dalaran.config.ModelInfo;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import org.apache.camel.model.ProcessorDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface DalaranModelTypeContext {

    void fromObject(@NotNull ProcessorDefinition route, @Nullable MessageModel model, @NotNull String modelType);

    void toObject(@NotNull ProcessorDefinition route, @Nullable MessageModel model, @NotNull String modelType);

    void fromObject(@NotNull ProcessorDefinition route, @NotNull MessageModel model);

    void toObject(@NotNull ProcessorDefinition route, @NotNull MessageModel model);

    void fromObject(@NotNull ProcessorDefinition route, @NotNull String modelType);

    void toObject(@NotNull ProcessorDefinition route, @NotNull String modelType);

    void addModelType(DalaranModelType modelType);

    Set<String> listAllModelType();

    Collection<ModelInfo> listAllModelInfo();

    ModelInfo getModelInfo(String modelType);

    DalaranModelType getModelType(String modelTypeName);

    Class<? extends DalaranModelSchema> getModelSchema(String modelTypeName);
}
