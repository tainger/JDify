package io.terminus.dalaran;

import io.terminus.dalaran.annotation.DalaranComponent;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class DalaranComponentLoader {

    private static final Map<String, DalaranComponentContainer<DalaranTrigger>> triggerMapping = new ConcurrentHashMap<>();
    private static final Map<String, DalaranComponentContainer<DalaranProcessor>> processorMapping = new ConcurrentHashMap<>();

    public static void loadComponents() {
        ServiceLoader.load(Component.class).forEach(component -> {
            Class componentClass = component.getClass();
            DalaranComponent dalaranComponent = (DalaranComponent) componentClass.getDeclaredAnnotation(DalaranComponent.class);
            if (dalaranComponent == null) {
                return;
            }
            String componentType = dalaranComponent.value();
            if (component instanceof DalaranTrigger) {
                DalaranComponentContainer<DalaranTrigger> componentContainer = new DalaranComponentContainer<>(componentType, componentClass, dalaranComponent.configType(), (DalaranTrigger) component);
                triggerMapping.put(componentType, componentContainer);
            } else if (component instanceof DalaranProcessor) {
                DalaranComponentContainer<DalaranProcessor> componentContainer = new DalaranComponentContainer<>(componentType, componentClass, dalaranComponent.configType(), (DalaranProcessor) component);
                processorMapping.put(componentType, componentContainer);
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
