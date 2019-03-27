package io.terminus.dalaran.support.component;

import io.terminus.dalaran.DalaranComponentContext;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.DalaranComponent;
import io.terminus.dalaran.annotation.Processor;
import io.terminus.dalaran.annotation.Trigger;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDalaranComponentContext implements DalaranComponentContext {

    private final Map<String, DalaranTrigger> triggerMapping = new ConcurrentHashMap<>();
    private final Map<String, DalaranProcessor> processorMapping = new ConcurrentHashMap<>();

    private final Map<String, Trigger> triggerInfoMapping = new ConcurrentHashMap<>();
    private final Map<String, Processor> processorInfoMapping = new ConcurrentHashMap<>();

    @Override
    public void addTrigger(String triggerType, Trigger triggerInfo, DalaranTrigger trigger) {
        triggerInfoMapping.put(triggerType, triggerInfo);
        triggerMapping.put(triggerType, trigger);
    }

    @Override
    public void addProcessor(String processorType, Processor processorInfo, DalaranProcessor processor) {
        processorInfoMapping.put(processorType, processorInfo);
        processorMapping.put(processorType, processor);
    }

    @Override
    public DalaranTrigger getTrigger(String triggerType) {
        return triggerMapping.get(triggerType);
    }

    @Override
    public DalaranProcessor getProcessor(String processorType) {
        return processorMapping.get(processorType);
    }

    @Override
    public Trigger getTriggerInfo(String triggerType) {
        return triggerInfoMapping.get(triggerType);
    }

    @Override
    public Processor getProcessorInfo(String processorType) {
        return processorInfoMapping.get(processorType);
    }

    @PostConstruct
    public void loadComponents() {
        // TODO 可以用 spring 的 annotation, 可以少些一个 service load file
        ServiceLoader.load(DalaranComponent.class).forEach(component -> {
            Class componentClass = component.getClass();
            if (component instanceof DalaranTrigger) {
                Trigger triggerInfo = (Trigger) componentClass.getDeclaredAnnotation(Trigger.class);
                String triggerType = triggerInfo.value();
                addTrigger(triggerType, triggerInfo, (DalaranTrigger) component);
            } else if (component instanceof DalaranProcessor) {
                Processor processorInfo = (Processor) componentClass.getDeclaredAnnotation(Processor.class);
                String processorType = processorInfo.value();
                addProcessor(processorType, processorInfo, (DalaranProcessor) component);
            }
        });
    }
}
