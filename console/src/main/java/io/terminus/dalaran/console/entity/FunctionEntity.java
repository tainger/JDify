package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.FunctionAbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_function")
public class FunctionEntity extends FunctionAbstractEntity {
}
