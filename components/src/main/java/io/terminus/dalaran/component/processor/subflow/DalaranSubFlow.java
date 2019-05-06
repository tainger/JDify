package io.terminus.dalaran.component.processor.subflow;

import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.Trigger;
import io.terminus.dalaran.config.AllModelConfig;
import org.apache.camel.model.RouteDefinition;

@Trigger(value = "sub-flow", configType = DalaranSubFlowConfig.class, serializedBody = false)
public class DalaranSubFlow extends AllModelConfig implements DalaranTrigger<DalaranSubFlowConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, DalaranSubFlowConfig config) {

    }
}
