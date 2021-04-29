package io.terminus.dalaran.config;

import lombok.Data;

import java.util.Map;

@Data
public class DalaranConfigField {

    private String name;

    private String inputType;

    private String reusableConfig;

    private String defaultValue;

    private String example;

    private String label;

    private boolean required;

    private boolean readonly;

    private boolean dynamic;

    private String path;

    private String param;

    Map<String, String> enumValues;

    private ValidateConfig validateConfig;
}
