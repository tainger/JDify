package io.terminus.dalaran.entity.basic;

import io.terminus.dalaran.entity.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class PropertyAbstractEntity extends BasicEntity {

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 256)
    private String value;

    @Column(columnDefinition = "TEXT")
    private String description;
}
