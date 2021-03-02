package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@MappedSuperclass
public abstract class AlarmRuleAbstractEntity extends BasicEntity {

    @Column(nullable = false)
    private Long moduleId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String config;
}
