package io.terminus.dalaran.component.csb.processor.webservice;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

@Processor(
        value = "WebServiceClient",
        order = 9,
        configType = WebServiceClientConfig.class,
        bodyType = "JSON",
        developer = DalaranConstants.DALARAN
)
public class WebServiceClient implements DalaranProcessor<WebServiceClientConfig> {

    @Override
    public void configure(ProcessorDefinition route, WebServiceClientConfig config) {
        route.process(new WebServiceProcessor(config));
    }

}
