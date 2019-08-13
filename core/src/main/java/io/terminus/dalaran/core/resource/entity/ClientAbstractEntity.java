package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class ClientAbstractEntity extends BasicEntity {

    @Column(nullable = false)
    private Long moduleId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String appKey;

    @Column(nullable = false)
    private String secret;

    @Column(columnDefinition = "TEXT")
    private String description;

}
