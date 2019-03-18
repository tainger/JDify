package io.terminus.dalaran.model;

import lombok.Data;

import java.util.Map;

@Data
public class DalaranComponentProperties {

    private String id;

    private String type;

    private Map<String, Object> config;
}
