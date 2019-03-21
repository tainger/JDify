package io.terminus.dalaran.console.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class TriggerEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private String config;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ModelEntity inModel;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ModelEntity outModel;
}
