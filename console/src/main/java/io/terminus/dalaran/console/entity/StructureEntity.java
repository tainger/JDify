package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.BodyModelType;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by jingdi on 2019/3/27
 */
@Data
@Entity
@Table(name = "dalaran_structure")
public class StructureEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private BodyModelType structureType;

    private String structureSchema;

    private String description;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    @CreatedBy
    private Date createdBy;

    @LastModifiedBy
    private Date updatedBy;
}
