package io.terminus.dalaran.impl;

import io.terminus.dalaran.DalaranComponentContainer;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.model.DalaranFlow;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

public class DefaultDalaranCamelContext implements DalaranContext {

    private final CamelContext camelContext;

    private final DalaranComponentContainer componentContainer;

    public DefaultDalaranCamelContext(DalaranComponentContainer componentContainer) {
        this.camelContext = new DefaultCamelContext();
        this.componentContainer = componentContainer;
        try {
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeFlow(String flowId) throws Exception {
        camelContext.removeRoute(flowId);
    }

    @Override
    public void removeFlows(List<String> flowIds) {
        flowIds.forEach(flowId -> {
            try {
                camelContext.removeRoute(flowId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void removeAllFlow() throws Exception {
        camelContext.removeRouteDefinitions(camelContext.getRouteDefinitions());
    }

    // TODO Flow 可以抽象一个 Builder, 但暂时没有必要
    @Override
    public void addFlow(DalaranFlow dalaranFlow) {
        val route = new RouteDefinition();
        // TODO route need an u id
        route.setId(dalaranFlow.getId());
        val trigger = dalaranFlow.getTrigger();
        val processorList = dalaranFlow.getProcessors();
        val triggerComponent = componentContainer.getTrigger(trigger.getType());
        // TODO check

        if (dalaranFlow.getRetryable() != null && dalaranFlow.getRetryable()) {
            route.onException(Throwable.class).maximumRedeliveries(dalaranFlow.getMaxRetry())
                    .redeliveryDelay(dalaranFlow.getRetryDelay());
        }
        route.from(triggerComponent.buildRouterUri(trigger.getConfig()));
        route.to("log:trigger[" + trigger.getId() + "]?showAll=true&multiline=true");
        for (DalaranFlow.Processor processor : processorList) {
            route.to("log:processor[" + processor.getId() + "]?showAll=true&multiline=true");
            val processorComponent = componentContainer.getProcessor(processor.getType());
            processorComponent.configure(route, processor.getConfig());
        }

        // TODO on exception...
        try {
            camelContext.addRouteDefinition(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFlows(List<DalaranFlow> flows) {
        flows.forEach(this::addFlow);
    }

    @Override
    public DalaranComponentContainer getDalaranComponentContainer() {
        return componentContainer;
    }
}
