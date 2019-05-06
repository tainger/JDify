package io.terminus.dalaran.entity;

import io.terminus.dalaran.converter.ListToJsonConverter;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/3/27
 */
@Data
@Entity
@Table(name = "dalaran_module")
public class ModuleEntity extends BasicEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Convert(converter = ListToJsonConverter.class)
    @Column(name = "dependency_ids")
    private List<Long> dependencies = new ArrayList<>();
}
