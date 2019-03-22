package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.BodyModelType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class ModelEntity {

    @Id
    @GeneratedValue
    private Long id;

    private BodyModelType modelType;

    private String modelSchema;
}
