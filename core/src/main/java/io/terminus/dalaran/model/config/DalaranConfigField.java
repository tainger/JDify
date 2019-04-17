package io.terminus.dalaran.model.config;

import io.terminus.dalaran.FieldInputType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DalaranConfigField {

    private String name;

    private FieldInputType inputType;

    private String defaultValue;

    private String example;

    private String label;

    private boolean isEnum;

    List<Map<String, String>> enumValues;
}
