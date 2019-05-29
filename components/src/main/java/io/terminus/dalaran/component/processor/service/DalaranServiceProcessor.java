package io.terminus.dalaran.component.processor.service;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranComponentConfigConverter;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.config.ServiceOperationConfig;
import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.component.model.ServiceOperation;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Processor(value = "service", configType = ServiceOperationConfig.class, serializeType = BodySerializeType.Serialized)
public class DalaranServiceProcessor implements DalaranProcessor<DalaranServiceOperation>, DalaranComponentConfigConverter<ServiceOperationConfig, DalaranServiceOperation> {

    private DalaranServiceContext serviceContext;

    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    public DalaranServiceProcessor(DalaranServiceContext serviceContext, DalaranResourceBuilder resourceBuilder) {
        this.serviceContext = serviceContext;
        this.resourceBuilder = resourceBuilder;
    }

    @Override
    public void configure(ProcessorDefinition route, DalaranServiceOperation config) {
        config.getDalaranService().configure(route, config.getOperationConfig());
    }

    @Override
    public DalaranServiceOperation convert(ServiceOperationConfig config, ProcessorModel processor, BasicFlow flow) {
        DalaranService dalaranService = serviceContext.getService(config.getServiceType());
        Object serviceConfig = resourceBuilder.buildServiceConfig(config.getServiceId());
        ServiceOperation operationConfig = dalaranService.getOperationConfig(serviceConfig, config.getOperation());
        DalaranServiceOperation serviceOperation = new DalaranServiceOperation();
        serviceOperation.setDalaranService(dalaranService);
        serviceOperation.setOperationConfig(operationConfig);
        return serviceOperation;
    }
}
