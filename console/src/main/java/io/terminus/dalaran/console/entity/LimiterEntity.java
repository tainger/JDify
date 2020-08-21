package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.LimiterAbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_limiter")
public class LimiterEntity extends LimiterAbstractEntity {
}
