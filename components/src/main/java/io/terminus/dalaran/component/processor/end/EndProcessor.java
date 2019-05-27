package io.terminus.dalaran.component.processor.end;

import io.terminus.dalaran.UnconfigurableDalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;


// TODO serializedBody
@Processor(value = "end", serializedBody = true)
public class EndProcessor implements UnconfigurableDalaranProcessor {
    @Override
    public void configure(ProcessorDefinition route) {
        route.stop();
    }
}
