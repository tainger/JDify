package io.terminus.dalaran.console.model;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
public class TriggerModel {

    @Nullable
    private Long id;

    private String name;

    private Long moduleId;

    private Long flowId;

    private String type;

    private Long inStructure;

    private Long outStructure;

    private Map<String, Object> config;

    private String description;
}
