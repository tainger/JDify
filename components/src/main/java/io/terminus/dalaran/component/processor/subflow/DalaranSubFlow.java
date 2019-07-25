package io.terminus.dalaran.component.processor.subflow;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import org.apache.camel.model.ProcessorDefinition;

@Processor(
        value = "sub-flow",
        name = "子流程",
        configType = DalaranSubFlowConfig.class
)
public class DalaranSubFlow implements DalaranProcessor<DalaranSubFlowConfig>, DalaranProcessorConfigCustomConverter<DalaranSubFlowConfig, DalaranSubFlowConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranSubFlowConfig config) {
        route.to(DalaranConstants.SUB_FLOW_DIRECT_PREFIX + config.getSubFlowId());
    }

    @Override
    public DalaranSubFlowConfig convert(DalaranSubFlowConfig config, ComponentModel processor, BasicFlow flow) {
        // TODO set in&out model?
        return config;
    }
}
