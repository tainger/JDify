package io.terminus.dalaran;

import org.apache.camel.model.RouteDefinition;

import java.util.Map;

public interface UnConfigurableDalaranExecutor extends DalaranExecutor {
    @Override
    default void configure(RouteDefinition route, Object config) {
        configure(route);
    }

    void configure(RouteDefinition route);

}
