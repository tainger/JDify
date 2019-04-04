package io.terminus.dalaran.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class ProcessorModel<T> {

    @NotNull
    private Long id;

    @NotNull
    private String type;

    @Nullable
    private T config;

    @Nullable
    private MessageModel inModel;

    @Nullable
    private MessageModel outModel;
}
