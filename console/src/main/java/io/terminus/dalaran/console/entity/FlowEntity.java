package io.terminus.dalaran.console.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class FlowEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @OneToMany
    private List<PropertyEntity> properties;

    @OneToOne
    private TriggerEntity trigger;

    @ManyToMany
    private List<ProcessorEntity> processors;
}
