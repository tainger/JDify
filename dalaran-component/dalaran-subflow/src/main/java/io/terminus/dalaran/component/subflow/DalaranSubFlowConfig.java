package io.terminus.dalaran.component.subflow;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ComponentModelConfig;
import lombok.Data;

@Data
public class DalaranSubFlowConfig extends ComponentModelConfig {

    @ConfigFieldInfo(label = "选择子流程", inputType = FieldInputType.SubFlow)
    private Long subFlowId;
}
