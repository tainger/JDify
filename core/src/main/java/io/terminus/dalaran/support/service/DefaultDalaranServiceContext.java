package io.terminus.dalaran.support.service;

import io.terminus.dalaran.DalaranService;
import io.terminus.dalaran.DalaranServiceContext;
import io.terminus.dalaran.annotation.ServiceConnector;
import io.terminus.dalaran.config.ServiceOperationConfig;
import io.terminus.dalaran.model.ServiceOperation;
import io.terminus.dalaran.model.config.ServiceInfo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDalaranServiceContext implements DalaranServiceContext, ApplicationContextAware {

    private ApplicationContext applicationContext;


    private final Map<String, DalaranService> serviceMapping = new ConcurrentHashMap<>();
    private final Map<String, ServiceInfo> serviceInfoMapping = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadComponents() {
        Map<String, DalaranService> serviceBeanMap = applicationContext.getBeansOfType(DalaranService.class);
        serviceBeanMap.values().forEach(bean -> {
            ServiceConnector serviceAnnotation = bean.getClass().getDeclaredAnnotation(ServiceConnector.class);
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setType(serviceAnnotation.value());
            serviceInfo.setImportConfigType(serviceAnnotation.importConfigType());
            serviceInfo.setServiceConfigType(serviceAnnotation.serviceConfigType());
            serviceMapping.put(serviceAnnotation.value(), bean);
            serviceInfoMapping.put(serviceAnnotation.value(), serviceInfo);
        });
    }

    @Override
    public DalaranService getService(String serviceType) {
        return serviceMapping.get(serviceType);
    }

    @Override
    public ServiceInfo getServiceInfo(String serviceType) {
        return serviceInfoMapping.get(serviceType);
    }

    @Override
    public ServiceOperation buildOperationConfig(String serviceType, Object serviceConfig, ServiceOperationConfig config) {
        return getService(serviceType).getOperationConfig(serviceConfig, config.getOperation());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
