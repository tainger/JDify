package io.terminus.dalaran.component.processor.foreach;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.camel.builder.Builder.body;

@Processor(
        value = "foreach",
        order = 20,
        configType = ForEachConfig.class
)
public class ForEachProcessor implements DalaranProcessor<String>, DalaranProcessorConfigCustomConverter<ForEachConfig, String> {

    @Autowired
    private DalaranContext<DalaranRoute> dalaranContext;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Override
    public void configure(ProcessorDefinition route, String fragmentUri) {
        route.split(body()).to(fragmentUri);
    }

    @Override
    public String convert(ForEachConfig config, ComponentModel component, BasicFlow flow) {
        FlowFragment fragment = resourceBuilder.buildFlowFragment(config.getPipeline(), component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId(), flow.isTracing());
        dalaranContext.addFragmentFlow(fragment);
        return fragment.getDirectRouteUri();
    }
}