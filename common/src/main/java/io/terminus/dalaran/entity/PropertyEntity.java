package io.terminus.dalaran.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_property")
public class PropertyEntity extends BasicEntity {

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 256)
    private String value;

    @Column(columnDefinition = "TEXT")
    private String description;
}
