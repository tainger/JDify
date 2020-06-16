package io.terminus.dalaran.mapper.model;

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
