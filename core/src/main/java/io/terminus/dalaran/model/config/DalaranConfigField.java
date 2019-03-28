package io.terminus.dalaran.model.config;

import io.terminus.dalaran.FieldInputType;
import lombok.Data;

@Data
public class DalaranConfigField {

    private String name;

    private FieldInputType inputType;

    private String defaultValue;

    private String example;

    private String label;

}
