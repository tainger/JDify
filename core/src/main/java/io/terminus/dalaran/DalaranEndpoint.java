package io.terminus.dalaran;

import org.apache.camel.model.RouteDefinition;

import java.util.Map;

public interface DalaranEndpoint<T> {
    void configure(RouteDefinition route, Map<String, String> properties, T config);
}
