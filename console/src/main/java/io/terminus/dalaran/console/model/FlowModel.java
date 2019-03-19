package io.terminus.dalaran.console.model;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class FlowModel {

    private Long id;

    private String name;

    private String description;

    private TriggerModel trigger;

    private Set<ProcessorModel> processors;

    private Map<String, String> properties;
}
