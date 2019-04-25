package io.terminus.dalaran.entity;

import io.terminus.dalaran.FlowStatus;
import io.terminus.dalaran.converter.ListToJsonConverter;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "dalaran_flow")
public class FlowEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @JoinColumn(name = "module_id")
    private Long moduleId;

    @Enumerated(EnumType.STRING)
    private FlowStatus status;

    private String description;

    private String triggerType;

    private String triggerConfig;

    /**
     * 依赖的所有 processor id list
     */
    @Convert(converter = ListToJsonConverter.class)
    @Column(name = "processor_ids")
    private List<Long> processorIds = new ArrayList<>();

    /**
     * processor 组成的 pipeline
     */
    @Convert(converter = ListToJsonConverter.class)
    @Column(name = "processing_pipeline")
    private List<Long> processingPipeline = new ArrayList<>();

    @JoinColumn(name = "in_structure")
    private Long inStructure;

    @JoinColumn(name = "out_structure")
    private Long outStructure;

    @CreatedDate
    @Column(columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    @LastModifiedDate
    @Column(columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
