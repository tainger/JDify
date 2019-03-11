package io.terminus.dalaran;

import io.terminus.dalaran.annotation.DalaranComponent;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class DalaranComponentLoader {

    private static final Map<String, DalaranComponentContainer<DalaranTrigger>> triggerMapping = new ConcurrentHashMap<>();
    private static final Map<String, DalaranComponentContainer<DalaranProcessor>> processorMapping = new ConcurrentHashMap<>();

    public static void loadComponents() {
        ServiceLoader.load(DalaranTrigger.class).forEach(trigger -> {
            Class componentClass = trigger.getClass();
            DalaranComponent dalaranComponent = (DalaranComponent) componentClass.getDeclaredAnnotation(DalaranComponent.class);
            if (dalaranComponent != null) {
                String triggerType = dalaranComponent.value();
                DalaranComponentContainer<DalaranTrigger> componentContainer = new DalaranComponentContainer<>(triggerType, componentClass, dalaranComponent.configType(), trigger);
                triggerMapping.put(triggerType, componentContainer);
            }
        });
        ServiceLoader.load(DalaranProcessor.class).forEach(processor -> {
            Class componentClass = processor.getClass();
            DalaranComponent dalaranComponent = (DalaranComponent) componentClass.getDeclaredAnnotation(DalaranComponent.class);
            if (dalaranComponent != null) {
                String processorType = dalaranComponent.value();
                DalaranComponentContainer<DalaranProcessor> componentContainer = new DalaranComponentContainer<>(processorType, componentClass, dalaranComponent.configType(), processor);
                processorMapping.put(processorType, componentContainer);
            }
        });

    }

    public static DalaranComponentContainer<DalaranTrigger> getTriggerContainer(String type) {
        return triggerMapping.get(type);
    }

    public static DalaranComponentContainer<DalaranProcessor> getProcessorContainer(String type) {
        return processorMapping.get(type);
    }

}
