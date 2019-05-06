package io.terminus.dalaran.console.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ConnectorDTO extends BasicConnectorInfo {

    private String description;

    private Map<String, Object> config;
}
