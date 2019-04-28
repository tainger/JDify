package io.terminus.dalaran.entity;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class ProcessorEntity {

    private String id;

    @NotNull
    private String type;

    @Nullable
    private String config;
}
