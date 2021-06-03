package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.dto.basic.BasicConnectorInfo;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
public class ConnectorDTO extends BasicConnectorInfo {

    private String description;

    @Nullable
    private Map<String, Object> config;

    private boolean isExist;

    private String nodeId;
}
