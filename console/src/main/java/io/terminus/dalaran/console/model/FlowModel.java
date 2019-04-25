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

    private Long moduleId;

    private String name;

    private String description;

    private StructureModel inStructure;

    private StructureModel outStructure;

    private String triggerType;

    private Map<String, Object> triggerConfig;

    private Set<ProcessorModel> processors;

    // TODO processor 有一个流内的唯一 ID, pipeline 就是由这个 ID 编排的, 该 ID 可以由前端生成, 因为一把保存的情况下, 前端无法知道  processor 保存的 ID 是什么
    private List<Long> processingPipeline;

}
