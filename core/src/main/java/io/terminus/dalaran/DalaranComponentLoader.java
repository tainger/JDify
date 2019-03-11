package io.terminus.dalaran;

import io.terminus.dalaran.annotation.DalaranComponent;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class DalaranComponentLoader {

    private static final Map<String, DalaranComponentContainer<DalaranTrigger>> listenerMapping = new ConcurrentHashMap<>();
    private static final Map<String, DalaranComponentContainer<DalaranProcessor>> endpointMapping = new ConcurrentHashMap<>();

    public static void loadComponents() {
        ServiceLoader.load(DalaranTrigger.class).forEach(listener -> {
            Class componentClass = listener.getClass();
            DalaranComponent dalaranComponent = (DalaranComponent) componentClass.getDeclaredAnnotation(DalaranComponent.class);
            if (dalaranComponent != null) {
                String listenerType = dalaranComponent.value();
                DalaranComponentContainer<DalaranTrigger> componentContainer = new DalaranComponentContainer<>(listenerType, componentClass, dalaranComponent.configType(), listener);
                listenerMapping.put(listenerType, componentContainer);
            }
        });
        ServiceLoader.load(DalaranProcessor.class).forEach(endpoint -> {
            Class componentClass = endpoint.getClass();
            DalaranComponent dalaranComponent = (DalaranComponent) componentClass.getDeclaredAnnotation(DalaranComponent.class);
            if (dalaranComponent != null) {
                String endpointType = dalaranComponent.value();
                DalaranComponentContainer<DalaranProcessor> componentContainer = new DalaranComponentContainer<>(endpointType, componentClass, dalaranComponent.configType(), endpoint);
                endpointMapping.put(endpointType, componentContainer);
            }
        });

    }

    public static DalaranComponentContainer<DalaranTrigger> getListenerContainer(String type) {
        return listenerMapping.get(type);
    }

    public static DalaranComponentContainer<DalaranProcessor> getEndpointContainer(String type) {
        return endpointMapping.get(type);
    }

}
