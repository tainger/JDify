package io.terminus.dalaran.core.resource;

import io.terminus.dalaran.core.flow.DalaranServiceLoader;
import io.terminus.dalaran.core.resource.entity.ServiceAbstractEntity;
import io.terminus.dalaran.model.component.ServiceInfo;
import org.apache.commons.lang3.StringUtils;

public class DefaultDalaranServiceLoader implements DalaranServiceLoader {

    private final DalaranResourceLoader resourceLoader;

    public DefaultDalaranServiceLoader(DalaranResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public ServiceInfo loadService(String serviceId) {
        if (StringUtils.isBlank(serviceId)) {
            return null;
        }
        ServiceAbstractEntity entity = resourceLoader.loadService(serviceId);
        return toDTO(entity);
    }

    private ServiceInfo toDTO(ServiceAbstractEntity entity) {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setModuleId(entity.getModuleId());
        serviceInfo.setName(entity.getName());
        serviceInfo.setImportConfig(entity.getImportConfig());
        serviceInfo.setServiceConfig(entity.getServiceConfig());
        serviceInfo.setType(entity.getType());
        serviceInfo.setDescription(entity.getDescription());
        return serviceInfo;
    }
}
