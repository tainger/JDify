package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.model.BodyType;
import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 * Created by jingdi on 2019/3/27
 */
@Data
@MappedSuperclass
public abstract class ModelAbstractEntity extends BasicEntity {

    @Column(nullable = false)
    private Long moduleId;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BodyType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String modelSchema;

    @Column(columnDefinition = "TEXT")
    private String description;
}
