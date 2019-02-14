package io.terminus.dalaran;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.List;
import java.util.Map;

public class MessageFlow extends RouteBuilder {

    private DalaranListener listener;

    private List<DalaranEndpoint> endpoints;

    private Map<String, String> properties;

    public DalaranListener getListener() {
        return listener;
    }

    public void setListener(DalaranListener listener) {
        this.listener = listener;
    }

    public List<DalaranEndpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<DalaranEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void configure() {
        RouteDefinition routeDefinition = from(listener.getUri(properties));
        for (DalaranEndpoint endpoint : endpoints) {
            endpoint.configure(routeDefinition, properties);
        }
    }
}
