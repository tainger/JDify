package io.terminus.dalaran;

import org.apache.camel.model.ProcessorDefinition;

public interface DalaranProcessor<T> {
    void configure(ProcessorDefinition route, T config);
}
