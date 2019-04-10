package io.terminus.dalaran.component.processor.route;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

@Processor(value = "router", bodyMode = BodyMode.Object, configType = DalaranRouterConfig.class)
public class DalaranRouter implements DalaranProcessor<DalaranRouterConfig> {
    public void configure(ProcessorDefinition route, DalaranRouterConfig config) {
//        ChoiceDefinition choice = route.choice();
//        for (DalaranRouterConfig.Route routeConfig : config.getRoutes()) {
//        }
    }
}
