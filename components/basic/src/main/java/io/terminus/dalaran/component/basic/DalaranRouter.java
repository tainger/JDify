package io.terminus.dalaran.component.basic;

import io.terminus.dalaran.DalaranComponentContainer;
import io.terminus.dalaran.DalaranComponentLoader;
import io.terminus.dalaran.DalaranProcessor;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ProcessorDefinition;

import static org.apache.camel.builder.Builder.header;

public class DalaranRouter implements DalaranProcessor<DalaranRouterConfig> {
    public void configure(ProcessorDefinition route, DalaranRouterConfig config) {
        ChoiceDefinition choice = route.choice();
        for (DalaranRouterConfig.Route routeConfig : config.getRoutes()) {
            // TODO 条件从 routeConfig.when 来, 这里需要规范一下输入
            ChoiceDefinition routeDefinition = choice.when(header("").isEqualTo(""));
            DalaranComponentContainer<DalaranProcessor> dalaranEndpointContainer = DalaranComponentLoader.getProcessorContainer(routeConfig.getProcessor().getType());
            // TODO config 替换掉参数配置
            dalaranEndpointContainer.getComponent().configure(routeDefinition, routeConfig.getProcessor().getConfig());
        }
    }
}
