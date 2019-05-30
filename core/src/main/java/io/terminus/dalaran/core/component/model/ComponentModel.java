package io.terminus.dalaran.core.component.model;

import io.terminus.dalaran.core.model.MessageModel;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class ComponentModel<T> {

    private String id;

    @NotNull
    private String type;

    @Nullable
    private T config;

    @Nullable
    private MessageModel inModel;

    @Nullable
    private MessageModel outModel;
}
