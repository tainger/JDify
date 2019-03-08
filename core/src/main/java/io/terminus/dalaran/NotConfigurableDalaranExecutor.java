package io.terminus.dalaran;

import org.apache.camel.model.RouteDefinition;

import java.util.Map;

public interface NotConfigurableDalaranExecutor extends DalaranExecutor {
    @Override
    default void configure(RouteDefinition route, Map properties, Object config) {
        configure(route, properties);
    }

    void configure(RouteDefinition route, Map<String, String> properties);

}
