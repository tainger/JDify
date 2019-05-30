package io.terminus.dalaran.core.resource.entity.released;

import io.terminus.dalaran.core.resource.entity.ModelAbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_model")
public class ModelReleasedEntity extends ModelAbstractEntity implements ReleasedEntity {

    @Column(nullable = false)
    private Long originId;

    @Column(nullable = false, length = 64)
    private String version;
}
