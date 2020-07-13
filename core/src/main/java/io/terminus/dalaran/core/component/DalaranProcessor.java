package io.terminus.dalaran.core.component;

import org.apache.camel.model.ProcessorDefinition;

public interface DalaranProcessor<T> extends DalaranComponent<T> {

    void configure(ProcessorDefinition route, T config);

}
