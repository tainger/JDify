package io.terminus.dalaran.component.processor.resilience4j;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

@Processor(
        order = 16,
        value = "Resilience4j",
        configType = Resilience4jConfig.class
)
public class Resilience4j implements DalaranProcessor<Resilience4jConfig> {

    @Override
    public void configure(ProcessorDefinition route, Resilience4jConfig config) {

    }
}
