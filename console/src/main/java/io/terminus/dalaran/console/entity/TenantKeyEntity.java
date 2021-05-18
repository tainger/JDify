package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_tenant_key")
public class TenantKeyEntity extends BasicEntity {

    private String resourceKey;

}
