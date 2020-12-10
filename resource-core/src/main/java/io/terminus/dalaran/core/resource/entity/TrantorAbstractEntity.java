package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class TrantorAbstractEntity extends BasicEntity {
    @Column(nullable = false, length = 64)
    private String moduleKey;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String integrationInfos;
}
