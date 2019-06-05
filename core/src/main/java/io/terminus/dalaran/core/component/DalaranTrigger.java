package io.terminus.dalaran.core.component;

import org.apache.camel.model.RouteDefinition;

public interface DalaranTrigger<T> extends DalaranComponent {
    void buildFromRoute(RouteDefinition route, T config);
}
