package io.terminus.dalaran.entity.manage;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class ProcessorEntity {

    private String id;

    @NotNull
    private String name;

    @NotNull
    private String type;

    @Nullable
    private String config;
}
