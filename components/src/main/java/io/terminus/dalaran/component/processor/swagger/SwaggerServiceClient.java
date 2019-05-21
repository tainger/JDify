package io.terminus.dalaran.component.processor.swagger;

import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import io.terminus.dalaran.service.swagger.SwaggerOperationConfig;
import org.apache.camel.model.ProcessorDefinition;

@Processor(
        value = "swagger-service",
        configType = SwaggerServiceClientConfig.class,
        serializedBody = true,
        service = true,
        allowBodyTypes = {BodyType.JSON, BodyType.XML}
)
public class SwaggerServiceClient implements DalaranProcessor<SwaggerOperationConfig> {

    @Override
    public void configure(ProcessorDefinition route, SwaggerOperationConfig config) {

        config.getPath()
    }
}
