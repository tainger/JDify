package io.terminus.dalaran.component.processor.foreach;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.component.ProcessorRouteInfo;
import lombok.Data;

import java.util.List;

@Data
public class ForEachConfig {

    @ConfigFieldInfo(inputType = FieldInputType.Pipeline)
    private List<ProcessorRouteInfo> pipeline;
}
