package io.terminus.dalaran.impl;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranComponentContainer;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranConverter;
import io.terminus.dalaran.impl.converter.JsonConverter;
import io.terminus.dalaran.impl.converter.XMLConverter;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.ModelType;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDalaranCamelContext implements DalaranContext {

    private final CamelContext camelContext;

    private final DalaranComponentContainer componentContainer;

    private final Map<ModelType, DalaranConverter> converterMapping;

    public DefaultDalaranCamelContext(DalaranComponentContainer componentContainer) {
        this.camelContext = new DefaultCamelContext();
        this.componentContainer = componentContainer;
        try {
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        converterMapping = new HashMap<>();

        // TODO 这个扩展面也很窄, 先写死吧...
        converterMapping.put(ModelType.JSON, new JsonConverter());
        converterMapping.put(ModelType.XML, new XMLConverter());
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
        val triggerComponent = componentContainer.getTrigger(trigger.getType());
        val triggerInfo = componentContainer.getTriggerInfo(trigger.getType());
        BodyMode currentBodyMode = triggerInfo.bodyMode();
        ModelType currentBodyType = null;
        if (trigger.getInModel() != null) {
            currentBodyType = trigger.getInModel().getModelType();
        }
        if (dalaranFlow.getRetryable() != null && dalaranFlow.getRetryable()) {
            route.onException(Throwable.class).maximumRedeliveries(dalaranFlow.getMaxRetry())
                    .redeliveryDelay(dalaranFlow.getRetryDelay());
        }
        triggerComponent.buildFromRoute(route, trigger.getConfig());
        route.to("log:trigger[" + trigger.getId() + "]?showAll=true&multiline=true");
        for (DalaranFlow.Processor processor : processorList) {
            val processorComponent = componentContainer.getProcessor(processor.getType());
            val processorInfo = componentContainer.getProcessorInfo(processor.getType());
            val nextBodyMode = processorInfo.bodyMode();
            if (currentBodyMode != nextBodyMode) {
                if (nextBodyMode == BodyMode.Serialized) {
                    assert processor.getInModel() != null;
                    unmarshal(route, processor.getInModel().getModelType());
                } else {
                    assert currentBodyType != null;
                    marshal(route, currentBodyType);
                }
            }
            currentBodyMode = processorInfo.bodyMode();
            // TODO check
            route.to("log:processor[" + processor.getId() + "]?showAll=true&multiline=true");
            processorComponent.configure(route, processor.getConfig());
        }

        if (currentBodyMode != triggerInfo.bodyMode()) {
            if (triggerInfo.bodyMode() == BodyMode.Serialized) {
                assert trigger.getOutModel() != null;
                unmarshal(route, trigger.getOutModel().getModelType());
            } else {
                assert currentBodyType != null;
                marshal(route, currentBodyType);
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
    public DalaranComponentContainer getDalaranComponentContainer() {
        return componentContainer;
    }

    // TODO 这里还要接 model, 一些特殊转换需要声明如何处理, 比如 XML
    private void unmarshal(RouteDefinition route, ModelType modelType) {
        converterMapping.get(modelType).fromObject(route);
    }

    // TODO 这里还要接 model, 一些特殊转换需要声明如何处理, 比如 XML
    private void marshal(RouteDefinition route, ModelType modelType) {
        converterMapping.get(modelType).toObject(route);
    }
}
