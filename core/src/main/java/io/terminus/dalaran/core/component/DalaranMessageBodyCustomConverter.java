package io.terminus.dalaran.core.component;

import org.apache.camel.model.RouteDefinition;

public interface DalaranMessageBodyCustomConverter<T> {

    boolean customConvert(RouteDefinition route, T config, boolean bodyIsSerialized);
}
