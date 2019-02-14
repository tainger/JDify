package io.terminus.dalaran;

import org.apache.camel.model.RouteDefinition;

import java.util.Map;

public interface DalaranEndpoint {
    void configure(RouteDefinition route, Map<String, String> properties);
}
