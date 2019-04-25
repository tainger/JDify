package io.terminus.dalaran.console.model;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
public class ProcessorModel {

    @Nullable
    private Long id;

    private String name;

    private String type;

    private Long moduleId;

    private Long inStructure;

    private Long outStructure;

    private Map<String, Object> config;

    private String description;
}
