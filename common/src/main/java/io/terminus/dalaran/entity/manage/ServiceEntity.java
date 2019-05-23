package io.terminus.dalaran.entity.manage;

import io.terminus.dalaran.entity.basic.ServiceAbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_service")
public class ServiceEntity extends ServiceAbstractEntity {
}
