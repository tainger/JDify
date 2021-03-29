package io.terminus.dalaran.model.component;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class ProcessorRouteInfo {

    private String id;

    @NotNull
    private String name;

    @NotNull
    private String type;

    private String group;

    private String version;

    @Nullable
    private String config;
}
