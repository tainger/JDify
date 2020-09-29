package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.common.BasicComponentType;
import lombok.Data;

@Data
public class ComponentDTO {

    private BasicComponentType type;

    private String config;
}
