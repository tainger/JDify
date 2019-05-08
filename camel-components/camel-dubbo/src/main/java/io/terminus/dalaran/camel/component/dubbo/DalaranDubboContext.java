package io.terminus.dalaran.camel.component.dubbo;

import java.util.HashMap;
import java.util.Map;

public class DalaranDubboContext {

    private final Map<DubboServiceId, DubboGenericProvider> providerMap = new HashMap<>();

    public DubboGenericProvider getProvider(String registryAddress, String serviceId, String version) {
        DubboServiceId dubboServiceId = new DubboServiceId();
        dubboServiceId.setRegistryAddress(registryAddress);
        dubboServiceId.setServiceId(serviceId);
        dubboServiceId.setVersion(version);
        return providerMap.get(dubboServiceId);
    }

    public DubboGenericProvider createProvider(DubboEndpoint endpoint) {
        DubboGenericProvider genericProvider = new DubboGenericProvider(endpoint);
        DubboServiceId dubboServiceId = new DubboServiceId();
        dubboServiceId.setRegistryAddress(endpoint.getRegistryAddress());
        dubboServiceId.setServiceId(endpoint.getServiceId());
        dubboServiceId.setVersion(endpoint.getVersion());
        providerMap.put(dubboServiceId, genericProvider);
        return genericProvider;
    }
}
