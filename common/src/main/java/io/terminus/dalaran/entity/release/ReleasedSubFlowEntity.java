package io.terminus.dalaran.entity.release;

import io.terminus.dalaran.entity.flow.SubFlowSuperEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_sub_flow")
public class ReleasedSubFlowEntity extends SubFlowSuperEntity implements ReleasedEntity {

    @Column(nullable = false)
    private Long originId;

    @Column(nullable = false, length = 64)
    private String version;
}
