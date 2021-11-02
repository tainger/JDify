package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.AuthenticatorAbstractEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_authenticator")
public class AuthenticatorEntity extends AuthenticatorAbstractEntity {

}
