package io.terminus.dalaran.core.resource.entity.basic;

import io.terminus.dalaran.core.resource.converter.PipelineJsonConverter;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.model.flow.FlowStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@MappedSuperclass
public class BasicFlowEntity extends BasicEntity {

    @Column(nullable = false)
    private Long moduleId;

    private Long inModel;

    private Long outModel;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private FlowStatus status;

    private boolean tracing;

    /**
     * all processor list
     */
    @Convert(converter = PipelineJsonConverter.class)
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private List<ProcessorEntity> pipeline;
}
