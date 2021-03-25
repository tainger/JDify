package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.SubFlowAbstractEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_private_sub_flow")
public class PrivateSubFlowEntity extends SubFlowAbstractEntity {
}
