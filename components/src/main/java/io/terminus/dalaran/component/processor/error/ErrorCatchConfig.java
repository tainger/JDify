package io.terminus.dalaran.component.processor.error;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import lombok.Data;

import java.util.List;

@Data
public class ErrorCatchConfig {
    @ConfigFieldInfo(inputType = FieldInputType.Pipeline)
    private List<ProcessorEntity> pipeline;

    @ConfigFieldInfo(inputType = FieldInputType.Pipeline)
    private List<ProcessorEntity> onErrorPipeline;
}
