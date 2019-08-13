package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.ClientAbstractEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_client")
public class ClientEntity extends ClientAbstractEntity {
}
