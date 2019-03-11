package io.terminus.dalaran;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

public class CamelRouteBuilder extends RouteBuilder {

    private DalaranFlow messageFlow;

    public CamelRouteBuilder(DalaranFlow messageFlow) {
        this.messageFlow = messageFlow;
    }

    @Override
    // TODO 先随便写一下让 demo 能通
    public void configure() {
        DalaranFlow.Listener listener = messageFlow.getListener();
        List<DalaranFlow.Endpoint> endpointList = messageFlow.getEndpoints();
        DalaranComponentContainer<DalaranTrigger> dalaranListenerContainer = DalaranComponentLoader.getListenerContainer(listener.getType());

        // TODO 替换 properties
        // TODO check
        RouteDefinition routeDefinition = from(dalaranListenerContainer.getComponent().buildRouterUri(listener.getConfig()));
        for (DalaranFlow.Endpoint endpoint : endpointList) {
            DalaranComponentContainer<DalaranProcessor> dalaranEndpointContainer = DalaranComponentLoader.getEndpointContainer(endpoint.getType());
            dalaranEndpointContainer.getComponent().configure(routeDefinition, endpoint.getConfig());
        }
    }
}
