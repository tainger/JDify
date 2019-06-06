package io.terminus.dalaran.component.processor.service;

import io.terminus.dalaran.component.processor.http.HttpClientConfig;
import io.terminus.dalaran.core.component.*;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.model.ComponentModel;
import io.terminus.dalaran.core.component.model.ServiceOperation;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.resource.entity.ServiceAbstractEntity;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Processor(
        value = "service", configType = ServiceOperationConfig.class,
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
public class DalaranServiceProcessor implements DalaranProcessor<DalaranServiceOperation>, DalaranDynamicBodySerializeType<HttpClientConfig>, DalaranProcessorConfigCustomConverter<ServiceOperationConfig, DalaranServiceOperation> {

    private final DalaranServiceContext serviceContext;

    private final DalaranResourceBuilder resourceBuilder;

    private final DalaranResourceLoader resourceLoader;

    @Autowired
    public DalaranServiceProcessor(DalaranServiceContext serviceContext, DalaranResourceBuilder resourceBuilder, DalaranResourceLoader resourceLoader) {
        this.serviceContext = serviceContext;
        this.resourceBuilder = resourceBuilder;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void configure(ProcessorDefinition route, DalaranServiceOperation config) {
        config.getDalaranService().configure(route, config.getOperationConfig());
    }

    @Override
    public BodySerializeType customBodySerializeType(HttpClientConfig config) {
        return null;
    }

    @Override
    public DalaranServiceOperation convert(ServiceOperationConfig config, ComponentModel component, BasicFlow flow) {
        ServiceAbstractEntity serviceEntity = resourceLoader.loadService(config.getServiceId());
        DalaranService dalaranService = serviceContext.getService(serviceEntity.getType());
        Object serviceConfig = resourceBuilder.buildServiceConfig(serviceEntity);
        ServiceOperation operationConfig = dalaranService.getOperationConfig(serviceConfig, config.getOperation());
        DalaranServiceOperation serviceOperation = new DalaranServiceOperation();
        serviceOperation.setDalaranService(dalaranService);
        serviceOperation.setOperationConfig(operationConfig);
        serviceOperation.setInModel(operationConfig.getInModel());
        serviceOperation.setOutModel(operationConfig.getOutModel());
        return serviceOperation;
    }
}
