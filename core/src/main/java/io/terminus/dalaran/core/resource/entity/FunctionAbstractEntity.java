package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.List;

@Data
@MappedSuperclass
public class FunctionAbstractEntity extends BasicEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String script;

    @Column(nullable = false, length = 256)
    private List<String> params;

}
