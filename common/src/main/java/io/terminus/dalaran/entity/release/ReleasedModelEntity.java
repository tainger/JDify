package io.terminus.dalaran.entity.release;

import io.terminus.dalaran.entity.ModelSuperEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_model")
public class ReleasedModelEntity extends ModelSuperEntity implements ReleasedEntity {

    @Column(nullable = false)
    private Long originId;

    @Column(nullable = false, length = 64)
    private String version;
}
