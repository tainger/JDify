package io.terminus.dalaran.component.trigger.dubbo;

import io.terminus.dalaran.BodySerializeType;
import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;

@Trigger(value = "dubbo-provider", configType = DubboProviderConfig.class, allowBodyTypes = {BodyType.OBJECT}, serializeType = BodySerializeType.Object)
public class DalaranDubboProvider implements DalaranTrigger<DubboProviderConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?application=%s&registryAddress=%s&serviceId=%s&method=%s&version=%s";

    @Override
    public void buildFromRoute(RouteDefinition route, DubboProviderConfig config) {
        String uri = String.format(DUBBO_PROVIDER_URI, config.getConnector().getApplication(), config.getConnector().getAddress(),
                config.getServiceId(), config.getMethod(), config.getVersion());
        route.from(uri);
    }
}
