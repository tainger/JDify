package io.terminus.dalaran.component.processor.error;

import io.terminus.dalaran.core.component.DalaranComponentValidator;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.model.component.ComponentModel;
import io.terminus.dalaran.model.flow.*;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Processor(
        value = "error-catch",
        configType = ErrorCatchConfig.class
)
public class ErrorCatch implements DalaranProcessor<String>, DalaranProcessorConfigCustomConverter<ErrorCatchConfig, String>, DalaranComponentValidator<ErrorCatchConfig> {

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
        List<ErrorCatchConfig.Route> routes = config.getRoutes();

        List<ProcessorEntity> pipeline = routes.get(0).getPipeline();
        List<ProcessorEntity> onErrorPipeline = routes.get(1).getPipeline();

        FlowFragment onErrorPipelineFragment = resourceBuilder.buildFlowFragment(onErrorPipeline, component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId() + "-on-error", flow.isTracing());
        dalaranContext.addFragmentFlow(onErrorPipelineFragment);

        FlowFragment fragment = resourceBuilder.buildFlowFragment(pipeline, component.getInModel(),
                component.getOutModel(), flow.getId(), component.getId(), flow.isTracing());
        DalaranRoute tryRoute = flowBuilder.buildFlowFragment(fragment);
        tryRoute.onException(Throwable.class).to(onErrorPipelineFragment.getDirectRouteUri()).continued(true);
        dalaranContext.addRoute(tryRoute);
        return fragment.getDirectRouteUri();
    }

    @Override
    public List<FlowValidation> validate(ErrorCatchConfig config) {
        List<FlowValidation> validations = new ArrayList<>();
        if (config.getRoutes().size() != 2) {
            FlowValidateMessage message = new FlowValidateMessage(ValidateMessageLevel.Error, "ERROR_CATCH_CONFIG_ERROR", "节点配置异常, 请删除该节点后重试");
            FlowValidation configError = FlowValidationBuilder.newBuilder().message(message).build();
            validations.add(configError);
        }
        return validations;
    }
}
