package io.terminus.dalaran.component.dubbo.provider;

import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.annotation.Component;
import org.apache.camel.model.RouteDefinition;

@Component(value = "dubbo-provider", configType = DubboProviderConfig.class, bodyMode = BodyMode.Object)
public class DalaranDubboProvider implements DalaranTrigger<DubboProviderConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?registryAddress=%s&serviceId=%s&method=%s&version=%s";

    @Override
    public void buildFromRoute(RouteDefinition route, DubboProviderConfig config) {
        String uri = String.format(DUBBO_PROVIDER_URI, config.getRegistryAddress(),
                config.getServiceId(), config.getMethod(), config.getVersion());
        route.from(uri);
    }
}
