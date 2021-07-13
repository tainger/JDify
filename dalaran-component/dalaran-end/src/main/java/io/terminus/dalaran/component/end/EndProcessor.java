package io.terminus.dalaran.component.end;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.UnconfigurableDalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

// TODO serializedBody
@Processor(
        value = "end",
        developer = DalaranConstants.DALARAN
)
public class EndProcessor implements UnconfigurableDalaranProcessor {
    @Override
    public void configure(ProcessorDefinition route) {
        route.stop();
    }
}
