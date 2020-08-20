package io.terminus.dalaran.core.component;

import io.terminus.dalaran.core.log.TracingErrorHandlerFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;

public interface DalaranCircuitBreaker<T> {

    void buildBreakerConfig(RouteDefinition route, String to, T config, CamelContext camelContext, TracingErrorHandlerFactory errorHandlerFactory);
}
