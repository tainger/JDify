package io.terminus.dalaran.console.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class TriggerEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private String config;
}
