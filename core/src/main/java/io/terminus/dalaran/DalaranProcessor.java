package io.terminus.dalaran;

import org.apache.camel.model.ProcessorDefinition;

public interface DalaranProcessor<T> extends DalaranComponent {
    void configure(ProcessorDefinition route, T config);
}
