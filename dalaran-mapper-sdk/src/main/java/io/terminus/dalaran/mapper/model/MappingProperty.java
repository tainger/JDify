package io.terminus.dalaran.mapper.model;

import io.terminus.dalaran.model.FieldType;
import lombok.Data;

/**
 * Created by jingdi on 2019/7/31
 */
@Data
public class MappingProperty {

    private boolean complex;

    private MappingStatus status;

    private FieldType fieldType;

    public MappingProperty() {
    }

    public MappingProperty(boolean complex, MappingStatus status, FieldType fieldType) {
        this.complex = complex;
        this.status = status;
        this.fieldType = fieldType;
    }
}
