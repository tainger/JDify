package io.terminus.dalaran.component.processor.error;

import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import lombok.Data;

import java.util.List;

@Data
public class ErrorCatchConfig {
    @ConfigFieldInfo(inputType = FieldInputType.ErrorCatch)
    private List<Route> routes;

    @Data
    class Route {
        private List<ProcessorEntity> pipeline;
    }
}
