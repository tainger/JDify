package io.terminus.dalaran.component.processor.service;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.DalaranService;
import io.terminus.dalaran.DalaranServiceContext;
import io.terminus.dalaran.annotation.Processor;
import io.terminus.dalaran.component.processor.sql.SqlConfig;
import io.terminus.dalaran.config.ServiceOperationConfig;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Processor(value = "service", configType = SqlConfig.class, serializedBody = true)
public class DalaranServiceProcessor implements DalaranProcessor<ServiceOperationConfig> {

    private DalaranServiceContext serviceContext;

    @Autowired
    public DalaranServiceProcessor(DalaranServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    @Override
    public void configure(ProcessorDefinition route, ServiceOperationConfig config) {
        DalaranService dalaranService = serviceContext.getService(config.getServiceType());
        dalaranService.configure(route, config.getOperationConfig());
    }
}
