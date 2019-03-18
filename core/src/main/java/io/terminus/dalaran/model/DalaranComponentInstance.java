package io.terminus.dalaran.model;

import lombok.Data;

import java.util.Map;

@Data
public class DalaranComponentInstance {
    private String type;
    private String configInstanceId;
    private Map<String, Object> config;


}
