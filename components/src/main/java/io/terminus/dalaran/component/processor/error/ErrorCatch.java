package io.terminus.dalaran.component.processor.error;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Processor(
        value = "error-catch",
        name = "失败重试",
        configType = ErrorCatchConfig.class
)
public class ErrorCatch implements DalaranProcessor<String>, DalaranProcessorConfigCustomConverter<ErrorCatchConfig, String> {

    @Autowired
    private DalaranContext<DalaranRoute> dalaranContext;

    @Autowired
    private DalaranFlowBuilder<DalaranRoute> flowBuilder;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Override
    public void configure(ProcessorDefinition route, String routeUrl) {
        route.to(routeUrl);
    }

    @Override
    public String convert(ErrorCatchConfig config, ComponentModel component, BasicFlow flow) {
        FlowFragment onErrorPipeline = resourceBuilder.buildFlowFragment(config.getOnErrorPipeline(), component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId() + "-on-error", flow.isTracing());
        dalaranContext.addFragmentFlow(onErrorPipeline);

        FlowFragment fragment = resourceBuilder.buildFlowFragment(config.getPipeline(), component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId(), flow.isTracing());
        DalaranRoute tryRoute = flowBuilder.buildFlowFragment(fragment);
        tryRoute.onException(Throwable.class).to(onErrorPipeline.getDirectRouteUri()).continued(true);
        dalaranContext.addRoute(tryRoute);
        return fragment.getDirectRouteUri();
    }
}
