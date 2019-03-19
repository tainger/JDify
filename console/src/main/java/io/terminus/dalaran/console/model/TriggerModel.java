package io.terminus.dalaran.console.model;

import lombok.Data;

import java.util.Map;

@Data
public class TriggerModel {
    private Long id;

    private String type;

    private Map<String, Object> config;
}
