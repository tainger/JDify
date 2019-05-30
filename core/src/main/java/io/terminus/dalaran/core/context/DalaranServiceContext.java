package io.terminus.dalaran.core.context;

import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.config.ServiceOperationConfig;
import io.terminus.dalaran.core.component.model.ServiceOperation;
import io.terminus.dalaran.core.config.ServiceInfo;

import java.util.Collection;

public interface DalaranServiceContext {

    DalaranService getService(String serviceType);

    ServiceInfo getServiceInfo(String serviceType);

    ServiceOperation buildOperationConfig(String serviceType, Object serviceConfig, ServiceOperationConfig config);

    Collection<ServiceInfo> getAllServiceInfo();
}
