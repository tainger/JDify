package io.terminus.dalaran.component.processor.route;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ProcessorDefinition;

@Processor(value = "router", serializedBody = false, configType = DalaranRouterConfig.class)
public class DalaranRouter implements DalaranProcessor<DalaranRouterConfig> {

    private static final String OTHERWISE_EXPRESSION = "otherwise";

    public void configure(ProcessorDefinition route, DalaranRouterConfig config) {
        ChoiceDefinition choiceDefinition = route.choice();
        for (DalaranRoute configRoute : config.getRoutes()) {
            choiceDefinition.when().el(configRoute.getExpression()).to(configRoute.getDisplayName()).stop();
            if (OTHERWISE_EXPRESSION.equals(configRoute.getExpression())) {
                choiceDefinition.otherwise().to(configRoute.getDisplayName());
            }
        }
        choiceDefinition.end();
    }
}
