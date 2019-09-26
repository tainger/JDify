package io.terminus.dalaran.component.processor.foreach;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import lombok.Data;

import java.util.List;

@Data
public class ForEachConfig {

    @ConfigFieldInfo(inputType = FieldInputType.Pipeline)
    private List<ProcessorEntity> pipeline;
}
