package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.console.JsonConverter;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jingdi on 2019/3/27
 */
@Data
@Entity
@Table(name = "dalaran_module")
public class ModuleEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @Convert(converter = JsonConverter.class)
    @Column(name = "dependency_ids")
    private List<Long> dependencies = new ArrayList<>();

    @CreatedDate
    @Column(columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    @LastModifiedDate
    @Column(columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
