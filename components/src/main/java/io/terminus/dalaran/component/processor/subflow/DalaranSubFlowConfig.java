package io.terminus.dalaran.component.processor.subflow;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ComponentModelConfig;
import lombok.Data;

@Data
public class DalaranSubFlowConfig extends ComponentModelConfig {

    @ConfigFieldInfo(label = "选择子流程", inputType = FieldInputType.SubFlow)
    private Long subFlowId;
}
