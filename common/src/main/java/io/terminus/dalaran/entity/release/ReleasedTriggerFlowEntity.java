package io.terminus.dalaran.entity.release;

import io.terminus.dalaran.entity.flow.TriggerFlowSuperEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_trigger_flow")
public class ReleasedTriggerFlowEntity extends TriggerFlowSuperEntity {
    private Long originId;

    private String version;
}
