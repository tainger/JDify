package io.terminus.dalaran.model.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
public class ProcessorDTO {

    private String id;

    @NotNull
    private String name;

    @NotNull
    private String type;

    private String group;

    private String version;

    @Nullable
    private Map<String, Object> config;

    public ProcessorDTO() {

    }
}