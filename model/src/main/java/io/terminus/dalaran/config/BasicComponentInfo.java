package io.terminus.dalaran.config;

import lombok.Data;

@Data
public class BasicComponentInfo {

    private String type;

    private String name;

    private int order;

    private DalaranConfigField[] configFields;
}
