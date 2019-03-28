package io.terminus.dalaran.support.flow;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranComponentContext;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranConverterContext;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.MessageModel;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

public class DefaultDalaranCamelContext implements DalaranContext {

    private final CamelContext camelContext;

    private DalaranConverterContext converterContext;

    private DalaranComponentContext componentContext;

    public DefaultDalaranCamelContext(DalaranConverterContext converterContext, DalaranComponentContext componentContext) {
        this.converterContext = converterContext;
        this.componentContext = componentContext;
        this.camelContext = new DefaultCamelContext();
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
        // TODO check
        val triggerComponent = componentContext.getTrigger(trigger.getType());
        val triggerInfo = componentContext.getTriggerInfo(trigger.getType());
        BodyMode currentBodyMode = triggerInfo.getBodyMode();
        MessageModel currentModel = null;
        if (trigger.getInModel() != null) {
            currentModel = trigger.getInModel();
        }
        if (dalaranFlow.getRetryable() != null && dalaranFlow.getRetryable()) {
            route.onException(Throwable.class).maximumRedeliveries(dalaranFlow.getMaxRetry())
                    .redeliveryDelay(dalaranFlow.getRetryDelay());
        }
        triggerComponent.buildFromRoute(route, trigger.getConfig());
        route.to("log:trigger[" + trigger.getId() + "]?showAll=true&multiline=true");
        for (DalaranFlow.Processor processor : processorList) {
            val processorComponent = componentContext.getProcessor(processor.getType());
            val processorInfo = componentContext.getProcessorInfo(processor.getType());
            val nextBodyMode = processorInfo.getBodyMode();
            if (currentBodyMode != nextBodyMode) {
                if (nextBodyMode == BodyMode.Serialized) {
                    assert processor.getInModel() != null;
                    converterContext.marshal(route, processor.getInModel());
                    currentModel = processor.getOutModel();
                } else {
                    assert currentModel != null;
                    converterContext.unmarshal(route, currentModel);
                }
            }
            currentBodyMode = processorInfo.getBodyMode();
            // TODO check
            route.to("log:processor[" + processor.getId() + "]?showAll=true&multiline=true");
            processorComponent.configure(route, processor.getConfig());
        }

        if (currentBodyMode != triggerInfo.getBodyMode()) {
            if (triggerInfo.getBodyMode() == BodyMode.Serialized) {
                assert trigger.getOutModel() != null;
                converterContext.marshal(route, trigger.getOutModel());
            } else {
                assert currentModel != null;
                converterContext.unmarshal(route, currentModel);
            }
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
    public DalaranComponentContext getDalaranComponentContext() {
        return componentContext;
    }

    @Override
    public DalaranConverterContext getDalaranConverterContext() {
        return converterContext;
    }
}
