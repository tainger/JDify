package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Component;
import org.apache.camel.model.ProcessorDefinition;

@Component(value = "router", configType = DalaranRouterConfig.class)
public class DalaranRouter implements DalaranProcessor<DalaranRouterConfig> {
    public void configure(ProcessorDefinition route, DalaranRouterConfig config) {
//        ChoiceDefinition choice = route.choice();
//        for (DalaranRouterConfig.Route routeConfig : config.getRoutes()) {
//        }
    }
}
