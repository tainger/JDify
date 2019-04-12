package io.terminus.dalaran.console.model;

import lombok.Data;

import java.util.Map;

@Data
public class ProcessorModel {
    private Long id;

    private String name;

    private String type;

    private Long moduleId;

    private Map<String, Object> config;

    private String description;
}
