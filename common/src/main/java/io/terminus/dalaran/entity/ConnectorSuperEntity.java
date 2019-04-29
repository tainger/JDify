package io.terminus.dalaran.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class ConnectorSuperEntity extends BasicEntity {

    @Column(nullable = false)
    private Long moduleId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String config;
}
