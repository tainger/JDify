package io.terminus.dalaran.core.resource.entity.released;

import io.terminus.dalaran.core.resource.entity.TrantorAbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_released_trantor_integration")
public class ReleasedTrantorEntity extends TrantorAbstractEntity {

    @Column(nullable = false)
    private Long originId;

    @Column(nullable = false, length = 64)
    private String version;
}
