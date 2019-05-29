package io.terminus.dalaran.core.component.model;

import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.model.MessageModel;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class TriggerModel<T> {
    @NotNull
    private Long id;

    @NotNull
    private BasicFlow flow;

    @NotNull
    private String type;

    @Nullable
    private T config;

    @Nullable
    private MessageModel inModel;

    @Nullable
    private MessageModel outModel;
}
