package io.terminus.dalaran.component.trigger.dubbo;

import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;

@Trigger(
        value = "dubbo-provider",
        order = 12,
        configType = DubboProviderConfig.class
)
public class DalaranDubboProvider implements DalaranTrigger<DubboProviderConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?application=%s&registryAddress=%s&serviceId=%s&method=%s&version=%s&timeout=%s&retries=%s&threads=%s";

    @Override
    public void buildFromRoute(RouteDefinition route, DubboProviderConfig config) {
        String uri = String.format(DUBBO_PROVIDER_URI, config.getConnector().getApplication(), config.getConnector().getAddress(),
                config.getServiceId(), config.getMethod(), config.getVersion(), config.getTimeout(), config.getRetries(), config.getConnector().getThreads());
        route.from(uri);
    }
}
