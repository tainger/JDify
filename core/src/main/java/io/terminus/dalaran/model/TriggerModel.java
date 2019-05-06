package io.terminus.dalaran.model;

import io.terminus.dalaran.model.flow.BasicFlow;
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
