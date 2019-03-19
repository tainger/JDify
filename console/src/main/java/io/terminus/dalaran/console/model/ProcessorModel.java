package io.terminus.dalaran.console.model;

import lombok.Data;

import java.util.Map;

@Data
public class ProcessorModel {
    private Long id;

    private String type;

    private Map<String, Object> config;
}
