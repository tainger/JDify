package io.terminus.dalaran.entity.flow;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_trigger_flow")
public class TriggerFlowEntity extends BasicFlowEntity {

    @Column(nullable = false)
    private String triggerType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String triggerConfig;

}
