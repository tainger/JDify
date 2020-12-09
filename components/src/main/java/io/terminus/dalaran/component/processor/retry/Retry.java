package io.terminus.dalaran.component.processor.retry;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.flow.DalaranFragmentBuilder;
import io.terminus.dalaran.model.RetryConvertFragmentInfo;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Processor(
        value = "retry",
        configType = RetryConfig.class,
        description = "重试节点：使该节点包含的部分流程在发生异常时，进行局部的流程重试"
)
public class Retry implements DalaranProcessor<RetryConvertFragmentInfo>, DalaranProcessorConfigCustomConverter<RetryConfig, RetryConvertFragmentInfo> {

    @Autowired
    private DalaranContext<DalaranRoute> dalaranContext;

    @Autowired
    private DalaranFlowBuilder<DalaranRoute> flowBuilder;

    @Autowired
    private DalaranFragmentBuilder fragmentBuilder;

    @Override
    public void configure(ProcessorDefinition route, RetryConvertFragmentInfo fragment) {
        route.to(fragment.getUri());
    }

    @Override
    public RetryConvertFragmentInfo convert(RetryConfig config, ComponentModel component, BasicFlow flow) {
        FlowFragment fragment = fragmentBuilder.buildFlowFragment(config.getPipeline(), component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId(), flow.isTracing());
        DalaranRoute retryRoute = flowBuilder.buildFlowFragment(fragment);
        retryRoute.onException(Throwable.class).maximumRedeliveries(config.getMaxRetry()).redeliveryDelay(config.getRetryDelay());
        dalaranContext.addRoute(retryRoute);
        RetryConvertFragmentInfo fragmentInfo = new RetryConvertFragmentInfo();
        fragmentInfo.setUri(fragment.getDirectRouteUri());
        if (StringUtils.isNotBlank(retryRoute.getLastBodyType())) {
            fragmentInfo.setOutModelType(retryRoute.getLastBodyType());
        } else {
            fragmentInfo.setOutModelType(retryRoute.getLastOutModel().getModelType());
        }
        return fragmentInfo;
    }
}
