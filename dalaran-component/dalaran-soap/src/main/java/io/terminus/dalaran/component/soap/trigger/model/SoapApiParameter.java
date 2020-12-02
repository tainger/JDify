package io.terminus.dalaran.component.soap.trigger.model;

import io.terminus.dalaran.model.FieldType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SoapApiParameter {
    private int level;
    private String description;
    private FieldType type;
    private Map<String, SoapApiParameter> subParameter = new HashMap<>();
}
