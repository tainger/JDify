package io.terminus.dalaran.component.trigger.rest.model;

import io.terminus.dalaran.model.FieldType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ApiParameter {
    private int level;
    private String description;
    private FieldType type;
    private Map<String, ApiParameter> subParameter = new HashMap<>();
}
