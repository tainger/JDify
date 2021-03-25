package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.FunctionAbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_function")
public class FunctionEntity extends FunctionAbstractEntity {

    @Column(columnDefinition="varchar(64) default NULL")
    private String createdFrom;
}
