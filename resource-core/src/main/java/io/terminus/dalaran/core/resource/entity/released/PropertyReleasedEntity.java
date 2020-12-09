package io.terminus.dalaran.core.resource.entity.released;

import io.terminus.dalaran.core.resource.entity.PropertyAbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_property")
public class PropertyReleasedEntity extends PropertyAbstractEntity implements ReleasedEntity {

    @Column(nullable = false)
    private Long originId;

    @Column(nullable = false, length = 64)
    private String version;
}
