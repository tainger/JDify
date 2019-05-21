package io.terminus.dalaran;

import io.terminus.dalaran.config.ServiceProcessorConfig;
import io.terminus.dalaran.model.config.ComponentInfo;
import io.terminus.dalaran.model.config.ServiceInfo;

public interface DalaranServiceContext {

    ServiceInfo getServiceInfo(String serviceType);

    Object getServiceConfig(String serviceType, ServiceProcessorConfig config);

    Object convertProcessorConfig(String type, Object serviceConfig, ServiceProcessorConfig config);
}
