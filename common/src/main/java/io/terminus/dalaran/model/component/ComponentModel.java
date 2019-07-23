package io.terminus.dalaran.model.component;

import io.terminus.dalaran.model.MessageModel;
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
