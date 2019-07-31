package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;

/**
 * Created by jingdi on 2019/7/31
 */
@Data
public class MappingProperty {

    private boolean complex;

    private MappingStatus status;

    public MappingProperty(boolean complex, MappingStatus status) {
        this.complex = complex;
        this.status = status;
    }
}
