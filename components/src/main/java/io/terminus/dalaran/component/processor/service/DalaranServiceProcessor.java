package io.terminus.dalaran.component.processor.service;

import io.terminus.dalaran.core.component.DalaranComponentValidator;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.config.ServiceOperationConfig;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.flow.DalaranServiceBuilder;
import io.terminus.dalaran.core.flow.DalaranServiceLoader;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.component.ServiceInfo;
import io.terminus.dalaran.model.component.ServiceOperation;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.flow.FlowValidationBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static io.terminus.dalaran.DalaranConstants.UNKNOWN_MODEL_TYPE;
import static io.terminus.dalaran.component.processor.service.ServiceValidationMessages.OPERATION_NOT_EXIST;
import static io.terminus.dalaran.component.processor.service.ServiceValidationMessages.SERVICE_NOT_EXIST;

@Processor(
        value = "service",
        order = 11,
        bodyType = UNKNOWN_MODEL_TYPE,
        configType = ServiceOperationConfig.class
)
public class DalaranServiceProcessor implements DalaranProcessor<DalaranServiceOperation>, DalaranProcessorConfigCustomConverter<ServiceOperationConfig, DalaranServiceOperation>, DalaranComponentValidator<ServiceOperationConfig> {

    private final DalaranServiceContext serviceContext;

    private final DalaranServiceBuilder serviceBuilder;

    private final DalaranServiceLoader serviceLoader;

    @Autowired
    public DalaranServiceProcessor(DalaranServiceContext serviceContext, DalaranServiceBuilder serviceBuilder, DalaranServiceLoader serviceLoader) {
        this.serviceContext = serviceContext;
        this.serviceBuilder = serviceBuilder;
        this.serviceLoader = serviceLoader;
    }

    @Override
    public void configure(ProcessorDefinition route, DalaranServiceOperation config) {
        config.getDalaranService().configure(route, config.getOperationConfig());
    }

    @Override
    public DalaranServiceOperation convert(ServiceOperationConfig config, ComponentModel component, BasicFlow flow) {
        ServiceInfo serviceEntity = serviceLoader.loadService(config.getServiceId());
        DalaranService dalaranService = serviceContext.getService(serviceEntity.getType());
        Object serviceConfig = serviceBuilder.buildServiceConfig(serviceEntity);
        ServiceOperation operationConfig = dalaranService.getOperationConfig(serviceConfig, config.getOperation());
        DalaranServiceOperation serviceOperation = new DalaranServiceOperation();
        serviceOperation.setDalaranService(dalaranService);
        serviceOperation.setOperationConfig(operationConfig);
        serviceOperation.setInModel(serviceBuilder.buildModel((operationConfig.getInModelId())));
        serviceOperation.setOutModel(serviceBuilder.buildModel(operationConfig.getOutModelId()));
        return serviceOperation;
    }

    @Override
    public List<FlowValidation> validate(ServiceOperationConfig config) {
        List<FlowValidation> messages = new ArrayList<>();
        if (config.getServiceId() == null) {
            return messages;
        }
        ServiceInfo serviceEntity = serviceLoader.loadService(config.getServiceId());
        if (serviceEntity == null) {
            messages.add(FlowValidationBuilder.newBuilder().field("serviceId").message(SERVICE_NOT_EXIST).build());
            return messages;
        }
        DalaranService dalaranService = serviceContext.getService(serviceEntity.getType());
        Object serviceConfig = serviceBuilder.buildServiceConfig(serviceEntity);
        ServiceOperation operationConfig = dalaranService.getOperationConfig(serviceConfig, config.getOperation());
        if (operationConfig == null) {
            messages.add(FlowValidationBuilder.newBuilder().field("operation").message(OPERATION_NOT_EXIST).build());
            return messages;
        }
        DalaranServiceOperation serviceOperation = new DalaranServiceOperation();
        serviceOperation.setInModel(serviceBuilder.buildModel((operationConfig.getInModelId())));
        serviceOperation.setOutModel(serviceBuilder.buildModel(operationConfig.getOutModelId()));
        return messages;
    }
}
