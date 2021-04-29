package io.terminus.dalaran.config;

import lombok.Data;

@Data
public class DynamicConfigTypeField {

    private String type;

    private DalaranConfigField[] configFields;
}
