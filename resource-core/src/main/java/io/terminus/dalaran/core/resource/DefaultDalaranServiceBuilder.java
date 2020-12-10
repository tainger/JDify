package io.terminus.dalaran.core.resource;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.core.flow.DalaranServiceBuilder;
import io.terminus.dalaran.core.resource.entity.ServiceAbstractEntity;
import io.terminus.dalaran.core.resource.entity.released.ServiceReleasedEntity;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ServiceInfo;

public class DefaultDalaranServiceBuilder implements DalaranServiceBuilder {

    private final DalaranResourceBuilder resourceBuilder;

    public DefaultDalaranServiceBuilder(DalaranResourceBuilder resourceBuilder) {
        this.resourceBuilder = resourceBuilder;
    }

    @Override
    public Object buildServiceConfig(ServiceInfo serviceInfo) {
        return resourceBuilder.buildServiceConfig(toEntity(serviceInfo));
    }

    @Override
    public MessageModel buildModel(Long modelId) {
        return resourceBuilder.buildModel(modelId);
    }

    private ServiceAbstractEntity toEntity(ServiceInfo serviceInfo) {
        return JSON.parseObject(JSON.toJSONString(serviceInfo), ServiceReleasedEntity.class);
    }
}
