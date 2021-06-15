package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class AuthenticatorAbstractEntity extends BasicEntity {

    @Column(nullable = false)
    private String moduleId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2550)
    private String config;

    @Column(nullable = false)
    private String type;
}
