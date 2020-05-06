package io.terminus.dalaran.core.resource.entity.released;

import io.terminus.dalaran.core.resource.entity.ConnectorAbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_connector", indexes={@Index(name = "originId", columnList = "originId")})
public class ConnectorReleasedEntity extends ConnectorAbstractEntity implements ReleasedEntity {

    @Column(nullable = false)
    private Long originId;

    @Column(nullable = false, length = 64)
    private String version;
}
