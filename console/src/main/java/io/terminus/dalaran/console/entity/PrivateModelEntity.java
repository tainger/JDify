package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.ModelAbstractEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_private_model")
public class PrivateModelEntity extends ModelAbstractEntity {
}
