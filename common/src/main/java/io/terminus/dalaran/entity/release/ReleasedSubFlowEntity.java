package io.terminus.dalaran.entity.release;

import io.terminus.dalaran.entity.flow.SubFlowSuperEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_sub_flow")
public class ReleasedSubFlowEntity extends SubFlowSuperEntity {
    private Long originId;

    private String version;
}
