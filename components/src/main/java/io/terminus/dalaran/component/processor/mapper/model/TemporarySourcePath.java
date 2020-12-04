package io.terminus.dalaran.component.processor.mapper.model;

import io.terminus.dalaran.core.component.model.ParamType;
import lombok.Data;

@Data
public class TemporarySourcePath {

    private ParamType type;

    private String value;

    public TemporarySourcePath(ParamType type, String value) {
        this.type = type;
        this.value = value;
    }
}
