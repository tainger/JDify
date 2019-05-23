package io.terminus.dalaran;

import io.terminus.dalaran.config.ImmutableModelConfig;
import io.terminus.dalaran.config.ServiceOperationConfig;
import io.terminus.dalaran.model.config.ServiceInfo;

public interface DalaranServiceContext {

    DalaranService getService(String serviceType);

    ServiceInfo getServiceInfo(String serviceType);

    ImmutableModelConfig buildOperationConfig(String serviceType, Object serviceConfig, ServiceOperationConfig config);

}
