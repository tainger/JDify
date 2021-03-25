package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.ConnectorAbstractEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_private_connector")
public class PrivateConnectorEntity extends ConnectorAbstractEntity {
}
