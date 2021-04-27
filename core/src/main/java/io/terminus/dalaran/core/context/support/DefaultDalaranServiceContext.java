package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.config.DalaranConfigField;
import io.terminus.dalaran.config.ServiceInfo;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.annotation.ServiceConnector;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.util.ConfigFieldUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDalaranServiceContext implements DalaranServiceContext, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<String, DalaranService> serviceMapping = new ConcurrentHashMap<>();
    private final Map<String, ServiceInfo> serviceInfoMapping = new ConcurrentHashMap<>();

    private final Map<String, String> serviceConfigClassMapping = new ConcurrentHashMap<>();


    @PostConstruct
    public void loadComponents() {
        Map<String, DalaranService> serviceBeanMap = applicationContext.getBeansOfType(DalaranService.class);
        serviceBeanMap.values().forEach(bean -> {
            ServiceConnector serviceAnnotation = bean.getClass().getDeclaredAnnotation(ServiceConnector.class);

            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setType(serviceAnnotation.value());
            serviceInfo.setImportConfigType(serviceAnnotation.importConfigType());
            serviceInfo.setServiceConfigType(serviceAnnotation.serviceConfigType());

            DalaranConfigField[] importConfigFields = ConfigFieldUtils.buildConfigFields(serviceAnnotation.importConfigType());
            serviceInfo.setConfigFields(importConfigFields);
            serviceConfigClassMapping.put(serviceAnnotation.value(), serviceAnnotation.serviceConfigType().getName());
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
    public Collection<ServiceInfo> getAllServiceInfo() {
        return serviceInfoMapping.values();
    }

    @Override
    public Map<String, String> getConfigClassMap() {
        return serviceConfigClassMapping;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
