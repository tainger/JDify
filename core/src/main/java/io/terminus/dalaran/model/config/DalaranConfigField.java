package io.terminus.dalaran.model.config;

import io.terminus.dalaran.FieldInputType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DalaranConfigField {

    private String name;

    private FieldInputType inputType;

    private String reusableConfig;

    private String defaultValue;

    private String example;

    private String label;

    Map<String, String> enumValues;
}
