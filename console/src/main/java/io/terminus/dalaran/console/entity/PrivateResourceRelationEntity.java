package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_resource_relation")
public class PrivateResourceRelationEntity extends BasicEntity {

    private String dependencyId;

    private String resourceId;

    private String resourceVersion;
}
