package io.terminus.dalaran.component.errorcatch;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import lombok.Data;

import java.util.List;

@Data
public class ErrorCatchConfig extends OutModelConfig {
    @ConfigFieldInfo(inputType = FieldInputType.ErrorCatch)
    private List<Route> routes;

    @Data
    class Route {
        private List<ProcessorEntity> pipeline;
    }
}
