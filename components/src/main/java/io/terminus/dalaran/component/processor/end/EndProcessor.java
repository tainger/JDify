package io.terminus.dalaran.component.processor.end;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.UnconfigurableDalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

// TODO serializedBody
@Processor(value = "end", serializeType = BodySerializeType.All)
public class EndProcessor implements UnconfigurableDalaranProcessor {
    @Override
    public void configure(ProcessorDefinition route) {
        route.stop();
    }
}
