package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.ServiceAbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dalaran_service")
public class ServiceEntity extends ServiceAbstractEntity {

    @Column(columnDefinition="varchar(64) default NULL")
    private String createdFrom;
}
