package io.terminus.dalaran.config;

import lombok.Data;

@Data
public class DynamicConfigInfo {

    private String name;

    private String origin;

    private String version;

    private DynamicConfigTypeField[] configTypeFields;
}
