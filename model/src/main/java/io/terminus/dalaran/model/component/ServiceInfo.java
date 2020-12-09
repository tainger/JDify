package io.terminus.dalaran.model.component;

import lombok.Data;

@Data
public class ServiceInfo {

    private Long moduleId;

    private String name;

    private String type;

    private String importConfig;

    private String serviceConfig;

    private String description;
}
