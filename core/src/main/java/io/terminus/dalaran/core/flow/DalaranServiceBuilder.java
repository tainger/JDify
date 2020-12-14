package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ServiceInfo;

public interface DalaranServiceBuilder {

    Object buildServiceConfig(ServiceInfo serviceInfo);

    MessageModel buildModel(String modelId);
}
