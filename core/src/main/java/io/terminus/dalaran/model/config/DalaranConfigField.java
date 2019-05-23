package io.terminus.dalaran.model.config;

import io.terminus.dalaran.FieldInputType;
import lombok.Data;

import java.util.Map;

@Data
public class DalaranConfigField {

    private String name;

    private FieldInputType inputType;

    private String reusableConfig;

    private String defaultValue;

    private String example;

    private String label;

    private boolean required;

    private boolean readonly;

    Map<String, String> enumValues;
}
