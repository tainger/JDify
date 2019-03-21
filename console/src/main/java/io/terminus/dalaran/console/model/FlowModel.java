package io.terminus.dalaran.console.model;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class FlowModel {

    private Long id;

    private String name;

    private String description;

    private Boolean retryable;

    private Integer maxRetry = 1;

    private Integer retryDelay = 3000;

    private TriggerModel trigger;

    private Set<ProcessorModel> processors;

    private Map<String, String> properties;
}
