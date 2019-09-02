package io.terminus.dalaran.component.processor.retry;

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
        value = "retry",
        name = "失败重试",
        configType = RetryConfig.class
)
public class Retry implements DalaranProcessor<String>, DalaranProcessorConfigCustomConverter<RetryConfig, String> {

    @Autowired
    private DalaranContext<DalaranRoute> dalaranContext;

    @Autowired
    private DalaranFlowBuilder<DalaranRoute> flowBuilder;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Override
    public void configure(ProcessorDefinition route, String fragmentUri) {
        route.to(fragmentUri);
    }

    @Override
    public String convert(RetryConfig config, ComponentModel component, BasicFlow flow) {
        FlowFragment fragment = resourceBuilder.buildFlowFragment(config.getPipeline(), component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId(), flow.isTracing());
        DalaranRoute retryRoute = flowBuilder.buildFlowFragment(fragment);
        retryRoute.onException(Throwable.class).maximumRedeliveries(config.getMaxRetry()).redeliveryDelay(config.getRetryDelay());
        dalaranContext.addRoute(retryRoute);
        return fragment.getDirectRouteUri();
    }
}
