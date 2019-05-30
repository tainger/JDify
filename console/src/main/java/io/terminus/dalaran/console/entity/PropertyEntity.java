package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.PropertyAbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by jingdi on 2019/3/27
 */
@Entity
@Table(name = "dalaran_property")
public class PropertyEntity extends PropertyAbstractEntity {
}
