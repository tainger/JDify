package io.terminus.dalaran;

import org.apache.camel.model.RouteDefinition;

public interface CustomConvert<T> {

    boolean customConvert(RouteDefinition route, T config, boolean bodyIsSerialized);
}
