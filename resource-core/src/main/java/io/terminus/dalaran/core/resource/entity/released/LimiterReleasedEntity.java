package io.terminus.dalaran.core.resource.entity.released;

import io.terminus.dalaran.core.resource.entity.LimiterAbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_limiter")
public class LimiterReleasedEntity extends LimiterAbstractEntity implements ReleasedEntity {

    @Column(nullable = false)
    private Long originId;

    @Column(nullable = false, length = 64)
    private String version;
}
