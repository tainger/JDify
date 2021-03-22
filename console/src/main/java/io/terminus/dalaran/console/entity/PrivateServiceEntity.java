package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.ServiceAbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_private_service")
public class PrivateServiceEntity extends ServiceAbstractEntity {
}
