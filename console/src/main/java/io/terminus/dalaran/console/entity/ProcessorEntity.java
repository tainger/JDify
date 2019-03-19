package io.terminus.dalaran.console.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ProcessorEntity {

    @Id
    @GeneratedValue
    private Long id;
}
