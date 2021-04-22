package io.terminus.dalaran.core.resource.entity.released;

import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_trigger_flow", indexes={@Index(name = "originId", columnList = "originId")})
public class TriggerFlowReleasedEntity extends TriggerFlowAbstractEntity implements ReleasedEntity {

    @Column(nullable = false)
    private String originId;

    @Column(nullable = false, length = 64)
    private String version;


}
