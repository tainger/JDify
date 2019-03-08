package io.terminus.dalaran;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

public class CamelRouteBuilder extends RouteBuilder {

    private Pipeline messageFlow;

    public CamelRouteBuilder(Pipeline messageFlow) {
        this.messageFlow = messageFlow;
    }

    @Override
    // TODO 先随便写一下让 demo 能通
    public void configure() {
        Pipeline.Listener listener = messageFlow.getListener();
        List<Pipeline.Endpoint> endpointList = messageFlow.getEndpoints();
        DalaranComponentContainer<DalaranTrigger> dalaranListenerContainer = DalaranComponentLoader.getListenerContainer(listener.getType());

        // TODO 替换 properties
        // TODO check
        RouteDefinition routeDefinition = from(dalaranListenerContainer.getComponent().buildFromUri(listener.getConfig()));
        for (Pipeline.Endpoint endpoint : endpointList) {
            DalaranComponentContainer<DalaranExecutor> dalaranEndpointContainer = DalaranComponentLoader.getEndpointContainer(endpoint.getType());
            dalaranEndpointContainer.getComponent().configure(routeDefinition, endpoint.getConfig());
        }
    }
}
