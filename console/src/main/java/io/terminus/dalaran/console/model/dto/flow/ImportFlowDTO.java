package io.terminus.dalaran.console.model.dto.flow;

import io.terminus.dalaran.console.model.dto.ImportInfo;
import lombok.Data;

import java.util.Map;

@Data
public class ImportFlowDTO extends ImportInfo {
    private String name;
    private String description;
    private String triggerType;
    private Map<String, Object> triggerConfig;
}
