package io.terminus.dalaran.component.trigger.dubbo;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang.StringUtils;

@Trigger(value = "dubbo-provider", configType = DubboProviderConfig.class, bodyMode = BodyMode.Object)
public class DalaranDubboProvider implements DalaranTrigger<DubboProviderConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?registryAddress=%s&serviceId=%s&method=%s&version=%s";

    @Override
    public void buildFromRoute(RouteDefinition route, DubboProviderConfig config) {
        String uri = String.format(DUBBO_PROVIDER_URI, config.getRegistryAddress(),
                config.getServiceId(), config.getMethod(), config.getVersion());
        if (StringUtils.isNotEmpty(config.getParameterType())) {
            uri += "&parameterType=" + config.getParameterType();
        }
        route.from(uri);
    }
}
