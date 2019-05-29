package io.terminus.dalaran.component.processor.retry;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranComponentConfigConverter;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.flow.model.FlowFragment;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import lombok.val;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Processor(value = "retry", serializeType = BodySerializeType.All, configType = RetryConfig.class)
public class Retry implements DalaranProcessor<String>, DalaranComponentConfigConverter<RetryConfig, String> {

    @Autowired
    private DalaranContext<RouteDefinition> dalaranContext;

    @Autowired
    private DalaranFlowBuilder<RouteDefinition> flowBuilder;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Override
    public void configure(ProcessorDefinition route, String fragmentUri) {
        route.to(fragmentUri);
    }

    @Override
    public String convert(RetryConfig config, ProcessorModel processor, BasicFlow flow) {
        MessageModel fragmentLastOutModel = processor.getOutModel();
        List<ProcessorModel> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : config.getPipeline()) {
            val processorModel = resourceBuilder.buildProcessorModel(processorEntity, fragmentLastOutModel, flow);
            fragmentLastOutModel = processorModel.getOutModel();
            pipeline.add(processorModel);
        }

        FlowFragment fragment = new FlowFragment();
        fragment.setId(flow.getId());
        fragment.setPipeline(pipeline);
        fragment.setInModel(processor.getOutModel());
        fragment.setOutModel(fragmentLastOutModel);

        RouteDefinition retryRoute = flowBuilder.buildFlowFragment(fragment);
        retryRoute.onException(Throwable.class).maximumRedeliveries(config.getMaxRetry()).redeliveryDelay(config.getRetryDelay());
        dalaranContext.addRoute(retryRoute);

        return fragment.getDirectRouteUri();
    }
}
