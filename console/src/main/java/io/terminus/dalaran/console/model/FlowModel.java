package io.terminus.dalaran.console.model;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class FlowModel {

    @Nullable
    private Long id;

    private String name;

    private Long moduleId;

    private String description;

    private StructureModel inStructure;

    private StructureModel outStructure;

    private Set<ProcessorModel> processors;

    private List<Long> processorIds;

    private Map<String, String> properties;

    private List<Long> propertyIds;
}
