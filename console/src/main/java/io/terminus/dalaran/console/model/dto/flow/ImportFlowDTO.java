package io.terminus.dalaran.console.model.dto.flow;

import io.terminus.dalaran.console.model.dto.ImportInfo;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import lombok.Data;

import java.util.Map;

@Data
public class ImportFlowDTO extends ImportInfo {
    private String name;
    private String description;
    private String triggerType;
    private Map<String, Object> triggerConfig;
    private String processorType;
    private Map<String, Object> processorConfig;
    private ModelDTO processorInModel;
    private ModelDTO processorOutModel;
}
