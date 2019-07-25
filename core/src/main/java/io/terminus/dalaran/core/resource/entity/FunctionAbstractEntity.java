package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.resource.converter.ListToJsonConverter;
import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import io.terminus.dalaran.model.function.MappingFunctionType;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@MappedSuperclass
public abstract class FunctionAbstractEntity extends BasicEntity {

    @Column(nullable = false)
    private Long moduleId;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MappingFunctionType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String script;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Convert(converter = ListToJsonConverter.class)
    @Column(nullable = false, length = 256)
    private List<String> params;

}
