package io.terminus.dalaran.core.component;

import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.flow.BasicFlow;

public interface DalaranProcessorConfigCustomConverter<T, R> {

    R convert(T config, ComponentModel processor, BasicFlow flow);
}
