package io.terminus.dalaran.console.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
public class FlowEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @OneToOne
    private TriggerEntity trigger;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ProcessorEntity> processors;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PropertyEntity> properties;
}
