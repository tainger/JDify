package io.terminus.dalaran.component.loopwhile;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import lombok.Data;

import java.util.List;

@Data
public class LoopWhileConfig {

    @ConfigFieldInfo(label = "循环条件", inputType = FieldInputType.String)
    private String expression;

    @ConfigFieldInfo(inputType = FieldInputType.Pipeline)
    private List<ProcessorEntity> pipeline;
}
