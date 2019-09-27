package io.terminus.dalaran.component.processor.loopwhile;

import io.terminus.dalaran.config.ProcessorInfo;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.component.ProcessorModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.camel.language.mvel.MvelExpression.mvel;

@Processor(
        value = "loop-while",
        name = "条件循环",
        order = 20,
        configType = LoopWhileConfig.class
)
public class LoopWhile implements DalaranProcessor<LoopWhileFragmentInfo>, DalaranProcessorConfigCustomConverter<LoopWhileConfig, LoopWhileFragmentInfo> {
    @Autowired
    private DalaranContext<DalaranRoute> dalaranContext;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Override
    public void configure(ProcessorDefinition route, LoopWhileFragmentInfo config) {
        route.loopDoWhile(mvel(config.getExpression())).to(config.getRouteId()).end();
    }

    @Override
    public LoopWhileFragmentInfo convert(LoopWhileConfig config, ComponentModel component, BasicFlow flow) {
        FlowFragment fragment = resourceBuilder.buildFlowFragment(config.getPipeline(), component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId(), flow.isTracing());
        dalaranContext.addFragmentFlow(fragment);
        if (!fragment.getPipeline().isEmpty()) {
            ProcessorModel lastProcessor = fragment.getPipeline().get(fragment.getPipeline().size() - 1);
            ProcessorInfo lastProcessorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(lastProcessor.getType());
            if (lastProcessorInfo.getBodyType() != BodyType.OBJECT && lastProcessor.getOutModel() != null) {
                DalaranRoute route = dalaranContext.getDalaranFlowBuilder().buildFlowFragment(fragment);
                dalaranContext.getDalaranConverterContext().toObject(route, lastProcessor.getOutModel(), lastProcessorInfo.getBodyType());
                try {
                    dalaranContext.removeFlow(fragment.getRouteId());
                    dalaranContext.addRoute(route);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new LoopWhileFragmentInfo(config.getExpression(), fragment.getDirectRouteUri());
    }

}
