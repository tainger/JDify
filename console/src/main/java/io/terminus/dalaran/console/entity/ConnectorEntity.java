package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.ConnectorAbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by jingdi on 2019/3/27
 */
@Entity
@Table(name = "dalaran_connector")
public class ConnectorEntity extends ConnectorAbstractEntity {
}
