package io.terminus.dalaran.entity.flow;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class TriggerFlowSuperEntity extends BasicFlowEntity {

    @Column(nullable = false)
    private String triggerType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String triggerConfig;

}
