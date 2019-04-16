package io.terminus.dalaran.entity;

import io.terminus.dalaran.support.jpa.ListToJsonConverter;
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

    private String status;

    private String description;

    private Boolean retryable;

    private Integer maxRetry;

    private Integer retryDelay;

//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id")
    private Long moduleId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "in_structure")
    private StructureEntity inStructure;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "out_structure")
    private StructureEntity outStructure;

    @Convert(converter = ListToJsonConverter.class)
    @Column(name = "processor_ids")
    private List<Long> processors = new ArrayList<>();

    @Convert(converter = ListToJsonConverter.class)
    private List<Long> properties = new ArrayList<>();

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
