package io.terminus.dalaran.core.context;

import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import org.apache.camel.model.ProcessorDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DalaranConverterContext {

    Class<? extends DalaranModelSchema> getSchemaType(@NotNull BodyType modelType);

    void fromObject(@NotNull ProcessorDefinition route, @Nullable MessageModel model, @NotNull BodyType modelType);

    void toObject(@NotNull ProcessorDefinition route, @Nullable MessageModel model, @NotNull BodyType modelType);

    void fromObject(@NotNull ProcessorDefinition route, @NotNull MessageModel model);

    void toObject(@NotNull ProcessorDefinition route, @NotNull MessageModel model);

    void fromObject(@NotNull ProcessorDefinition route, @NotNull BodyType modelType);

    void toObject(@NotNull ProcessorDefinition route, @NotNull BodyType modelType);
}
