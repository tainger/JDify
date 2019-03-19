package io.terminus.dalaran.impl;

import io.terminus.dalaran.*;
import io.terminus.dalaran.annotation.DalaranComponent;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.util.DalaranPropertyUtils;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;

import java.util.List;
import java.util.ServiceLoader;

public abstract class AbstractDalaranCamelContext implements DalaranContext {

    private CamelContext camelContext = new DefaultCamelContext();

    private DalaranComponentContainer componentContainer;

    // TODO Flow 可以抽象一个 Builder, 但暂时没有必要
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

    public void addTrigger(String triggerType, Class configType, DalaranTrigger trigger) {
        componentContainer.addTrigger(triggerType, configType, trigger);
    }

    public void addProcessor(String processorType, Class configType, DalaranProcessor processor) {
        componentContainer.addProcessor(processorType, configType, processor);
    }

    @Override
    public void loadComponents() {
        // TODO 可以用 spring 的 annotation, 可以少些一个 service load file
        ServiceLoader.load(Component.class).forEach(component -> {
            Class componentClass = component.getClass();
            DalaranComponent dalaranComponent = (DalaranComponent) componentClass.getDeclaredAnnotation(DalaranComponent.class);
            if (dalaranComponent == null) {
                return;
            }
            String componentType = dalaranComponent.value();
            DalaranPropertyUtils.registerConfigType(dalaranComponent.configType());
            if (component instanceof DalaranTrigger) {
                addTrigger(componentType, dalaranComponent.configType(), (DalaranTrigger) component);
            } else if (component instanceof DalaranProcessor) {
                addProcessor(componentType, dalaranComponent.configType(), (DalaranProcessor) component);
            }
        });
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }

    public DalaranComponentContainer getComponentContainer() {
        return componentContainer;
    }

    public void setComponentContainer(DalaranComponentContainer componentContainer) {
        this.componentContainer = componentContainer;
    }
}
