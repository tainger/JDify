package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.ModelAbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by jingdi on 2019/3/27
 */
@Entity
@Table(name = "dalaran_model")
public class ModelEntity extends ModelAbstractEntity {

    @Column(columnDefinition="varchar(64) default NULL")
    private String createdFrom;
}
