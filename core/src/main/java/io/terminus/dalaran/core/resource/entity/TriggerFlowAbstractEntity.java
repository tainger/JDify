package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicFlowEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class TriggerFlowAbstractEntity extends BasicFlowEntity {

    @Column(nullable = false)
    private String triggerType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String triggerConfig;

}
