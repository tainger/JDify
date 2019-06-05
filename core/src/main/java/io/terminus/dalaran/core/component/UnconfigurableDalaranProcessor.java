package io.terminus.dalaran.core.component;

import org.apache.camel.model.ProcessorDefinition;

public interface UnconfigurableDalaranProcessor extends DalaranProcessor {
    @Override
    default void configure(ProcessorDefinition route, Object config) {
        configure(route);
    }

    void configure(ProcessorDefinition route);

}
