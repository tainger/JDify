package io.terminus.dalaran;

import org.apache.camel.model.RouteDefinition;

import java.util.Map;

public interface NotConfigurableDalaranEndpoint extends DalaranEndpoint {
    @Override
    default void configure(RouteDefinition route, Map properties, Object config) {
        configure(route, properties);
    }

    void configure(RouteDefinition route, Map<String, String> properties);

}
