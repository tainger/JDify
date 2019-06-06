package io.terminus.dalaran.component.processor.service;

import io.terminus.dalaran.component.processor.http.HttpClientConfig;
import io.terminus.dalaran.core.component.*;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.config.ServiceOperationConfig;
import io.terminus.dalaran.core.component.model.ComponentModel;
import io.terminus.dalaran.core.component.model.ServiceOperation;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Processor(
        value = "service", configType = ServiceOperationConfig.class,
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
public class DalaranServiceProcessor implements DalaranProcessor<DalaranServiceOperation>, DalaranDynamicBodySerializeType<HttpClientConfig>, DalaranProcessorConfigCustomConverter<ServiceOperationConfig, DalaranServiceOperation> {

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
    public BodySerializeType customBodySerializeType(HttpClientConfig config) {
        return null;
    }

    @Override
    public DalaranServiceOperation convert(ServiceOperationConfig config, ComponentModel component, BasicFlow flow) {
        DalaranService dalaranService = serviceContext.getService(config.getServiceType());
        Object serviceConfig = resourceBuilder.buildServiceConfig(config.getServiceId());
        ServiceOperation operationConfig = dalaranService.getOperationConfig(serviceConfig, config.getOperation());
        DalaranServiceOperation serviceOperation = new DalaranServiceOperation();
        serviceOperation.setDalaranService(dalaranService);
        serviceOperation.setOperationConfig(operationConfig);
        serviceOperation.setInModel(operationConfig.getInModel());
        serviceOperation.setOutModel(operationConfig.getOutModel());
        return serviceOperation;
    }
}
