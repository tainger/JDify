package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.model.component.ServiceInfo;

public interface DalaranServiceLoader {

    ServiceInfo loadService(Long serviceId);
}
