package io.terminus.dalaran.impl;

import io.terminus.dalaran.DalaranComponentContainer;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.util.DalaranPropertyUtils;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

public class DefaultDalaranCamelContext implements DalaranContext {

    private CamelContext camelContext = new DefaultCamelContext();

    private DalaranComponentContainer componentContainer;

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
        val triggerConfigType = componentContainer.getTriggerConfigType(trigger.getType());
        // TODO check

        // TODO 临时扔一下, 这部分要抽出去, config 也需要 cache
        Object config = DalaranPropertyUtils.convertConfig(trigger.getConfig(), dalaranFlow.getProperties(), triggerConfigType);
        route.from(triggerComponent.buildRouterUri(config));
        for (DalaranFlow.Processor processor : processorList) {
            val processorComponent = componentContainer.getProcessor(trigger.getType());
            val processorConfigType = componentContainer.getProcessorConfigType(trigger.getType());
            Object processorConfig = DalaranPropertyUtils.convertConfig(processor.getConfig(), dalaranFlow.getProperties(), processorConfigType);
            processorComponent.configure(route, processorConfig);
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
