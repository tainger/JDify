package io.terminus.dalaran.support.service;

import io.terminus.dalaran.DalaranService;
import io.terminus.dalaran.DalaranServiceContext;
import io.terminus.dalaran.config.ServiceProcessorConfig;
import io.terminus.dalaran.model.config.ServiceInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDalaranServiceContext implements DalaranServiceContext {

    private final Map<String, DalaranService> serviceMapping = new ConcurrentHashMap<>();

    @Override
    public ServiceInfo getServiceInfo(String serviceType) {
        return null;
    }

    @Override
    public Object getServiceConfig(String serviceType, ServiceProcessorConfig serviceConfig) {
        DalaranService dalaranService = serviceMapping.get(serviceType);
        serviceConfig.getServiceId();


        return dalaranService.configure(null, serviceConfig);
    }
}
