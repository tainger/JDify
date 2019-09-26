package io.terminus.dalaran.core.context;

import io.terminus.dalaran.config.ServiceInfo;
import io.terminus.dalaran.core.component.DalaranService;

import java.util.Collection;

public interface DalaranServiceContext {

    DalaranService getService(String serviceType);

    ServiceInfo getServiceInfo(String serviceType);

    Collection<ServiceInfo> getAllServiceInfo();
}
