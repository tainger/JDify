package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.message.ModelType;
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

    private ModelType modelType;
}
