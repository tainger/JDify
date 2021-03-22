package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.SubFlowAbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_sub_flow")
public class SubFlowEntity extends SubFlowAbstractEntity {

    @Column(columnDefinition="varchar(64) default NULL")
    private String createdFrom;
}
