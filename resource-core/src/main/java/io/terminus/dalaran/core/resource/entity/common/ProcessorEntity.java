package io.terminus.dalaran.core.resource.entity.common;

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

    private String group;

    private String version;

    private String key;

    @Nullable
    private String config;


    public ProcessorEntity() {

    }
}
