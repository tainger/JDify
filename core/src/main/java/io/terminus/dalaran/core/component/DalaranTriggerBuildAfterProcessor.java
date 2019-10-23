package io.terminus.dalaran.core.component;

import org.apache.camel.model.RouteDefinition;

public interface DalaranTriggerBuildAfterProcessor<T> {
    void buildAfter(RouteDefinition route, T config);
}
