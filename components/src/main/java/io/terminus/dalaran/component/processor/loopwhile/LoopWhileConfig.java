package io.terminus.dalaran.component.processor.loopwhile;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.component.ProcessorRouteInfo;
import lombok.Data;

import java.util.List;

@Data
public class LoopWhileConfig {

    @ConfigFieldInfo(label = "循环条件", inputType = FieldInputType.String)
    private String expression;

    @ConfigFieldInfo(inputType = FieldInputType.Pipeline)
    private List<ProcessorRouteInfo> pipeline;
}
