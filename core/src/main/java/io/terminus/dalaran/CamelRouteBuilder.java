package io.terminus.dalaran;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

public class CamelRouteBuilder extends RouteBuilder {

    private MessageFlow messageFlow;

    public CamelRouteBuilder(MessageFlow messageFlow) {
        this.messageFlow = messageFlow;
    }

    @Override
    // TODO 先随便写一下让 demo 能通
    public void configure() {
        MessageFlow.Listener listener = messageFlow.getListener();
        List<MessageFlow.Endpoint> endpointList = messageFlow.getEndpoints();
        DalaranComponentContainer<DalaranListener> dalaranListenerContainer = DalaranComponentLoader.getListenerContainer(listener.getType());

        // TODO check
        RouteDefinition routeDefinition = from(dalaranListenerContainer.getComponent().getUri(messageFlow.getProperties(), listener.getConfig()));
        for (MessageFlow.Endpoint endpoint : endpointList) {
            DalaranComponentContainer<DalaranEndpoint> dalaranEndpointContainer = DalaranComponentLoader.getEndpointContainer(endpoint.getType());
            dalaranEndpointContainer.getComponent().configure(routeDefinition, messageFlow.getProperties(), endpoint.getConfig());
        }
    }
}
