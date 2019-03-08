package io.terminus.dalaran;

import org.apache.camel.model.RouteDefinition;

import java.util.Map;

public interface DalaranExecutor<T> {
    void configure(RouteDefinition route, T config);
}
