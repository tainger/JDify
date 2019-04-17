package io.terminus.dalaran.console.model;

import lombok.Data;

import java.util.Map;

@Data
public class ProcessorModel {
    private Long id;

    private String name;

    private String type;

    private Long moduleId;

    private Long inStructureId;

    private StructureModel inStructure;

    private Long outStructureId;

    private StructureModel outStructure;

    private Map<String, Object> config;

    private String description;
}
