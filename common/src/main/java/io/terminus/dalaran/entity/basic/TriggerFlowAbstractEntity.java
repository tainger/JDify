package io.terminus.dalaran.entity.basic;

import io.terminus.dalaran.entity.BasicFlowEntity;
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
