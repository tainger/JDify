package io.terminus.dalaran.component.processor.service;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.resource.entity.ServiceAbstractEntity;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.component.ServiceOperation;
import io.terminus.dalaran.model.flow.BasicFlow;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Processor(
        value = "service",
        name = "服务调用器",
        configType = ServiceOperationConfig.class,
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
public class DalaranServiceProcessor implements DalaranProcessor<DalaranServiceOperation>, DalaranProcessorConfigCustomConverter<ServiceOperationConfig, DalaranServiceOperation> {

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
