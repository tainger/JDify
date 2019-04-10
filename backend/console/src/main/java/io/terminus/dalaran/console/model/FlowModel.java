package io.terminus.dalaran.console.model;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class FlowModel {

    private Long id;

    private String name;

    private Long moduleId;

    private String description;

    private Boolean retryable;

    private Integer maxRetry = 1;

    private Integer retryDelay = 3000;

//    private TriggerModel trigger;

    private Set<ProcessorModel> processors;

    private List<Long> processorIds;

    private Map<String, String> properties;

    private List<Long> propertyIds;
}
