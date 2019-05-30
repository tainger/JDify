package io.terminus.dalaran.component.processor.subflow;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

@Processor(
        value = "sub-flow", configType = DalaranSubFlowConfig.class
)
public class DalaranSubFlow implements DalaranProcessor<DalaranSubFlowConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranSubFlowConfig config) {

    }
}
