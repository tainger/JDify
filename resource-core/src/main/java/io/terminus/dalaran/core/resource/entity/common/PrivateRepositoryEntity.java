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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String group;

    @Column(nullable = false)
    private String tenantCode;

    @Column(nullable = false)
    private String logoUri;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String data;

    @Column(columnDefinition = "TEXT")
    private String description;
}
