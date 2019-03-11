package io.terminus.dalaran;

import org.apache.camel.model.RouteDefinition;

public interface UnconfigurableDalaranProcessor extends DalaranProcessor {
    @Override
    default void configure(RouteDefinition route, Object config) {
        configure(route);
    }

    void configure(RouteDefinition route);

}
