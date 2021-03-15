package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class AuthenticatorAbstractEntity extends BasicEntity {

    @Column(nullable = false)
    private String moduleId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthenticatorKeyLocation keyLocation;

    @Column(nullable = false)
    private boolean isStatic;

    @Column(nullable = false)
    private String authenticatorKey;

    @Column(nullable = false)
    private String authenticatorValue;

    private String expireTime;
}
