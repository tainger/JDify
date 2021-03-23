package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.ConnectorAbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by jingdi on 2019/3/27
 */
@Data
@Entity
@Table(name = "dalaran_connector")
public class ConnectorEntity extends ConnectorAbstractEntity {

    @Column(columnDefinition="varchar(64) default NULL")
    private String createdFrom;
}
