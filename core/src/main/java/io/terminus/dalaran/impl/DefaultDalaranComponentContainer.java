package io.terminus.dalaran.impl;

import io.terminus.dalaran.Component;
import io.terminus.dalaran.DalaranComponentContainer;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.DalaranComponent;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDalaranComponentContainer implements DalaranComponentContainer {

    private final Map<String, DalaranTrigger> triggerMapping = new ConcurrentHashMap<>();
    private final Map<String, DalaranProcessor> processorMapping = new ConcurrentHashMap<>();

    private final Map<String, DalaranComponent> triggerInfoMapping = new ConcurrentHashMap<>();
    private final Map<String, DalaranComponent> processorInfoMapping = new ConcurrentHashMap<>();


    @Override
    public void addTrigger(String triggerType, DalaranComponent componentInfo, DalaranTrigger trigger) {
        triggerInfoMapping.put(triggerType, componentInfo);
        triggerMapping.put(triggerType, trigger);
    }

    @Override
    public void addProcessor(String processorType, DalaranComponent componentInfo, DalaranProcessor processor) {
        processorInfoMapping.put(processorType, componentInfo);
        processorMapping.put(processorType, processor);
    }

    @Override
    public DalaranTrigger getTrigger(String type) {
        return triggerMapping.get(type);
    }

    @Override
    public DalaranProcessor getProcessor(String type) {
        return processorMapping.get(type);
    }

    @Override
    public DalaranComponent getTriggerInfo(String type) {
        return triggerInfoMapping.get(type);
    }

    @Override
    public DalaranComponent getProcessorInfo(String type) {
        return processorInfoMapping.get(type);
    }

    @PostConstruct
    public void loadComponents() {
        // TODO 可以用 spring 的 annotation, 可以少些一个 service load file
        ServiceLoader.load(Component.class).forEach(component -> {
            Class componentClass = component.getClass();
            DalaranComponent dalaranComponent = (DalaranComponent) componentClass.getDeclaredAnnotation(DalaranComponent.class);
            if (dalaranComponent == null) {
                return;
            }
            String componentType = dalaranComponent.value();
            if (component instanceof DalaranTrigger) {
                addTrigger(componentType, dalaranComponent, (DalaranTrigger) component);
            } else if (component instanceof DalaranProcessor) {
                addProcessor(componentType, dalaranComponent, (DalaranProcessor) component);
            }
        });
    }
}
