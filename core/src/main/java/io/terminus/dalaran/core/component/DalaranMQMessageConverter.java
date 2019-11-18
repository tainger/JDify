package io.terminus.dalaran.core.component;

import org.apache.camel.model.RouteDefinition;

public interface DalaranMQMessageConverter<T> extends DalaranComponent<T> {
    void customBodyConvert(RouteDefinition route, T config, String currentBodyType);
}
