package io.terminus.dalaran;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class DalaranComponentLoader {

    private static final Map<String, DalaranComponentContainer<DalaranListener>> listenerMapping = new ConcurrentHashMap<>();
    private static final Map<String, DalaranComponentContainer<DalaranEndpoint>> endpointMapping = new ConcurrentHashMap<>();

    public static void loadComponents() {
        ServiceLoader.load(DalaranListener.class).forEach(listener -> {
            Class componentClass = listener.getClass();
            DalaranComponent dalaranComponent = (DalaranComponent) componentClass.getDeclaredAnnotation(DalaranComponent.class);
            if (dalaranComponent != null) {
                String listenerType = dalaranComponent.value();
                DalaranComponentContainer<DalaranListener> componentContainer = new DalaranComponentContainer<>(listenerType, componentClass, dalaranComponent.configType(), listener);
                listenerMapping.put(listenerType, componentContainer);
            }
        });
        ServiceLoader.load(DalaranEndpoint.class).forEach(endpoint -> {
            Class componentClass = endpoint.getClass();
            DalaranComponent dalaranComponent = (DalaranComponent) componentClass.getDeclaredAnnotation(DalaranComponent.class);
            if (dalaranComponent != null) {
                String endpointType = dalaranComponent.value();
                DalaranComponentContainer<DalaranEndpoint> componentContainer = new DalaranComponentContainer<>(endpointType, componentClass, dalaranComponent.configType(), endpoint);
                endpointMapping.put(endpointType, componentContainer);
            }
        });

    }

    public static DalaranComponentContainer<DalaranListener> getListenerContainer(String type) {
        return listenerMapping.get(type);
    }

    public static DalaranComponentContainer<DalaranEndpoint> getEndpointContainer(String type) {
        return endpointMapping.get(type);
    }

}
