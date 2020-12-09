package io.terminus.dalaran.component.processor.foreach;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.flow.DalaranFragmentBuilder;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.camel.builder.Builder.body;

@Processor(
        value = "foreach",
        order = 20,
        configType = ForEachConfig.class,
        description = "For循环：模拟For循环，循环节点会将数组类型的数据自动切分，循环执行"
)
public class ForEachProcessor implements DalaranProcessor<String>, DalaranProcessorConfigCustomConverter<ForEachConfig, String> {

    @Autowired
    private DalaranContext<DalaranRoute> dalaranContext;

    @Autowired
    private DalaranFragmentBuilder fragmentBuilder;

    @Override
    public void configure(ProcessorDefinition route, String fragmentUri) {
        route.split(body()).to(fragmentUri);
    }

    @Override
    public String convert(ForEachConfig config, ComponentModel component, BasicFlow flow) {
        FlowFragment fragment = fragmentBuilder.buildFlowFragment(config.getPipeline(), component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId(), flow.isTracing());
        dalaranContext.addFragmentFlow(fragment);
        return fragment.getDirectRouteUri();
    }
}