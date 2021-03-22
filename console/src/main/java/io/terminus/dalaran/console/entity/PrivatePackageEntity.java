package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_private_package")
public class PrivatePackageEntity extends BasicEntity {

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 64)
    private String version;

    @Column(nullable = false, length = 256)
    private String filePath;

    @Column(nullable = false, length = 64)
    private String type;

    @Column(nullable = false, length = 64)
    private String resourceGroup;

    @Column(nullable = false, length = 64)
    private String tenantCode;
}
