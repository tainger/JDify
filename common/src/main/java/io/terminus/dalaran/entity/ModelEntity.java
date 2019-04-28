package io.terminus.dalaran.entity;

import io.terminus.dalaran.BodyType;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by jingdi on 2019/3/27
 */
@Data
@Entity
@Table(name = "dalaran_model")
public class ModelEntity extends BasicEntity {

    @Column(nullable = false)
    private Long moduleId;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BodyType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String modelSchema;

    @Column(columnDefinition = "TEXT")
    private String description;
}
