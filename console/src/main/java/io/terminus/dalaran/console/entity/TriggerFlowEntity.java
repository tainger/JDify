package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_trigger_flow")
public class TriggerFlowEntity extends TriggerFlowAbstractEntity {

    @Column(columnDefinition="varchar(64) default NULL")
    private String createdFrom;
}
