package io.terminus.dalaran.entity.basic;

import io.terminus.dalaran.entity.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class ServiceAbstractEntity extends BasicEntity {

    @Column(nullable = false)
    private Long moduleId;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 64)
    private String type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String importConfig;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String serviceConfig;

    @Column(columnDefinition = "TEXT")
    private String description;
}

