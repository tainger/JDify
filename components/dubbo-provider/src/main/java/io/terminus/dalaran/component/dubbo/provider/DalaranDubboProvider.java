package io.terminus.dalaran.component.dubbo.provider;

import io.terminus.dalaran.annotation.DalaranComponent;
import io.terminus.dalaran.DalaranTrigger;

@DalaranComponent(value = "dubbo-provider", configType = DubboProviderConfig.class)
public class DalaranDubboProvider implements DalaranTrigger<DubboProviderConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?registryAddress=%s&serviceId=%s&method=%s&version=%s";

    public String buildRouterUri(DubboProviderConfig config) {
        return String.format(DUBBO_PROVIDER_URI, config.getRegistryAddress(), config.getServiceId(), config.getMethod(), config.getVersion());
    }
}
