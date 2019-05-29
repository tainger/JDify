package io.terminus.dalaran.component.processor.subflow;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import org.apache.camel.model.ProcessorDefinition;

@Processor(value = "sub-flow", configType = DalaranSubFlowConfig.class, serializeType = BodySerializeType.All)
public class DalaranSubFlow extends AllModelConfig implements DalaranProcessor<DalaranSubFlowConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranSubFlowConfig config) {

    }
}
