package io.terminus.dalaran.core.resource.entity.released;

import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_trigger_flow")
public class TriggerFlowReleasedEntity extends TriggerFlowAbstractEntity implements ReleasedEntity {

    @Column(nullable = false)
    private Long originId;

    @Column(nullable = false, length = 64)
    private String version;
}
