package io.terminus.dalaran.core.resource.entity.common;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_private_repository")
public class PrivateRepositoryEntity extends BasicEntity {

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 64)
    private String version;

    @Column(nullable = false, length = 64)
    private String type;

    @Column(nullable = false, length = 64)
    private String resourceGroup;

    @Column(nullable = false, length = 64)
    private String tenantCode;

    @Column
    private String logoUri;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String data;

    @Column(columnDefinition = "TEXT")
    private String description;
}
