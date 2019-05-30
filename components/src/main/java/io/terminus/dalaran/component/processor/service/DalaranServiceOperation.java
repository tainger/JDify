package io.terminus.dalaran.component.processor.service;

import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.config.ImmutableModelConfig;
import io.terminus.dalaran.core.component.model.ServiceOperation;
import lombok.Data;

@Data
public class DalaranServiceOperation extends ImmutableModelConfig {

    private DalaranService dalaranService;
    private ServiceOperation operationConfig;
}
