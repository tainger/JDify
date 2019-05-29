package io.terminus.dalaran.core.component;

import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.flow.model.BasicFlow;

public interface DalaranComponentConfigConverter<T, R> {

    R convert(T config, ProcessorModel processor, BasicFlow flow);
}
