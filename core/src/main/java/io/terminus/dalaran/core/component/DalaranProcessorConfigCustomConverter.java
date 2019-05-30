package io.terminus.dalaran.core.component;

import io.terminus.dalaran.core.component.model.ComponentModel;
import io.terminus.dalaran.core.flow.model.BasicFlow;

public interface DalaranProcessorConfigCustomConverter<T, R> {

    R convert(T config, ComponentModel processor, BasicFlow flow);
}
