package io.terminus.dalaran.core.component;

import io.terminus.dalaran.core.flow.model.BasicFlow;

public interface DalaranTriggerFlowConfigCustomConverter<T, R> {

    R convert(T config, BasicFlow flow);
}
