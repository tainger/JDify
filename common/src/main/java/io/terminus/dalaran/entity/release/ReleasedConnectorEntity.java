package io.terminus.dalaran.entity.release;

import io.terminus.dalaran.entity.ConnectorSuperEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_connector")
public class ReleasedConnectorEntity extends ConnectorSuperEntity implements ReleasedEntity {

    @Column(nullable = false)
    private Long originId;

    @Column(nullable = false, length = 64)
    private String version;
}
