package io.terminus.dalaran.component.processor.route;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ProcessorDefinition;

@Processor(value = "router", configType = DalaranRouterConfig.class)
public class DalaranRouter implements DalaranProcessor<DalaranRouterConfig> {

    private static final String OTHERWISE_EXPRESSION = "otherwise";

    public void configure(ProcessorDefinition route, DalaranRouterConfig config) {
        ChoiceDefinition choiceDefinition = route.choice();
        for (DalaranRouterConfig.Route configRoute : config.getRoutes()) {
            if (OTHERWISE_EXPRESSION.equals(configRoute.getExpression())) {
                choiceDefinition.otherwise();
            } else {
                choiceDefinition.when().mvel(configRoute.getExpression());
            }
            choiceDefinition.to(configRoute.getFragmentUri());
        }
        choiceDefinition.end();
    }
}
