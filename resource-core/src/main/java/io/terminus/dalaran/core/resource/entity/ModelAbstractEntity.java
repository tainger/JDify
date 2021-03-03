package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import io.terminus.dalaran.model.ModelTargetType;
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

    @Column
    private String moduleId;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column
    private String targetId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModelTargetType targetType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String modelSchema;

    @Column(columnDefinition = "TEXT")
    private String description;
}
