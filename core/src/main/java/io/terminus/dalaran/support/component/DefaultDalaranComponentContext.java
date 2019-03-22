package io.terminus.dalaran.support.component;

import io.terminus.dalaran.DalaranComponentContext;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.DalaranComponent;
import io.terminus.dalaran.annotation.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDalaranComponentContext implements DalaranComponentContext {

    private final Map<String, DalaranTrigger> triggerMapping = new ConcurrentHashMap<>();
    private final Map<String, DalaranProcessor> processorMapping = new ConcurrentHashMap<>();

    private final Map<String, Component> triggerInfoMapping = new ConcurrentHashMap<>();
    private final Map<String, Component> processorInfoMapping = new ConcurrentHashMap<>();

    @Override
    public void addTrigger(String triggerType, Component componentInfo, DalaranTrigger trigger) {
        triggerInfoMapping.put(triggerType, componentInfo);
        triggerMapping.put(triggerType, trigger);
    }

    @Override
    public void addProcessor(String processorType, Component componentInfo, DalaranProcessor processor) {
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
    public Component getTriggerInfo(String type) {
        return triggerInfoMapping.get(type);
    }

    @Override
    public Component getProcessorInfo(String type) {
        return processorInfoMapping.get(type);
    }

    @PostConstruct
    public void loadComponents() {
        // TODO 可以用 spring 的 annotation, 可以少些一个 service load file
        ServiceLoader.load(DalaranComponent.class).forEach(component -> {
            Class componentClass = component.getClass();
            Component dalaranComponent = (Component) componentClass.getDeclaredAnnotation(Component.class);
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
