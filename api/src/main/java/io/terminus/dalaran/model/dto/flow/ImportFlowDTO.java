package io.terminus.dalaran.model.dto.flow;

import io.terminus.dalaran.model.dto.ImportInfo;
import io.terminus.dalaran.model.dto.ModelDTO;
import lombok.Data;

import java.util.Map;

@Data
public class ImportFlowDTO extends ImportInfo {
    private String name;
    private String description;
    private String triggerType;
    private Map<String, Object> triggerConfig;
    private String processorType;
    private String processorGroup;
    private String processorVersion;
    private Map<String, Object> processorConfig;
    private ModelDTO processorInModel;
    private ModelDTO processorOutModel;
}
