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
public class ErrorCatch implements DalaranProcessor<ErrorCatch.ErrorCatchRoutes>, DalaranProcessorConfigCustomConverter<ErrorCatchConfig, ErrorCatch.ErrorCatchRoutes> {

    @Autowired
    private DalaranContext<DalaranRoute> dalaranContext;

    @Autowired
    private DalaranFlowBuilder<DalaranRoute> flowBuilder;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Override
    public void configure(ProcessorDefinition route, ErrorCatchRoutes routes) {
        route.to(routes.pipelineUri).onException(Throwable.class).to(routes.onErrorUri);
    }

    @Override
    public ErrorCatchRoutes convert(ErrorCatchConfig config, ComponentModel component, BasicFlow flow) {
        ErrorCatchRoutes routes = new ErrorCatchRoutes();
        FlowFragment pipeline = resourceBuilder.buildFlowFragment(config.getPipeline(), component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId(), flow.isTracing());
        FlowFragment onErrorPipeline = resourceBuilder.buildFlowFragment(config.getPipeline(), component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId(), flow.isTracing());
        dalaranContext.addFragmentFlow(pipeline);
        dalaranContext.addFragmentFlow(onErrorPipeline);
        routes.pipelineUri = pipeline.getDirectRouteUri();
        routes.onErrorUri = onErrorPipeline.getDirectRouteUri();
        return routes;
    }

    static class ErrorCatchRoutes {
        String pipelineUri;
        String onErrorUri;
    }

}
