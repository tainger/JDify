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
        DalaranTriggerConfig trigger = messageFlow.getTrigger();
        List<DalaranProcessorConfig> processorList = messageFlow.getProcessors();
        DalaranComponentContainer<DalaranTrigger> dalaranListenerContainer = DalaranComponentLoader.getTriggerContainer(trigger.getType());

        // TODO 替换 properties
        // TODO check
        RouteDefinition routeDefinition = from(dalaranListenerContainer.getComponent().buildRouterUri(trigger.getConfig()));
        for (DalaranProcessorConfig processor : processorList) {
            DalaranComponentContainer<DalaranProcessor> dalaranEndpointContainer = DalaranComponentLoader.getProcessorContainer(processor.getType());
            dalaranEndpointContainer.getComponent().configure(routeDefinition, processor.getConfig());
        }
    }
}
