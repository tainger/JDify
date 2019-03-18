package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.DalaranComponent;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ProcessorDefinition;

@DalaranComponent(value = "router", configType = DalaranRouterConfig.class)
public class DalaranRouter implements DalaranProcessor<DalaranRouterConfig> {
    public void configure(ProcessorDefinition route, DalaranRouterConfig config) {
//        ChoiceDefinition choice = route.choice();
//        for (DalaranRouterConfig.Route routeConfig : config.getRoutes()) {
//        }
    }
}
