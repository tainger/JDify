package io.terminus.dalaran.core.config;

import io.terminus.dalaran.core.component.FieldInputType;
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
