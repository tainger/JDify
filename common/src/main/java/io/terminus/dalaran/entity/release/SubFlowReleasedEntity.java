package io.terminus.dalaran.entity.release;

import io.terminus.dalaran.entity.ReleasedEntity;
import io.terminus.dalaran.entity.basic.SubFlowAbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_sub_flow")
public class SubFlowReleasedEntity extends SubFlowAbstractEntity implements ReleasedEntity {

    @Column(nullable = false)
    private Long originId;

    @Column(nullable = false, length = 64)
    private String version;
}
