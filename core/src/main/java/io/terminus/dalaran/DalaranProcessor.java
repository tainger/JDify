package io.terminus.dalaran;

import org.apache.camel.model.RouteDefinition;

import java.util.Map;

public interface DalaranProcessor<T> {
    void configure(RouteDefinition route, T config);
}
