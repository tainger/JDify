package io.terminus.dalaran.component.trigger.dubbo;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.model.BodyType;
import org.apache.camel.model.RouteDefinition;

@Trigger(
        value = "dubbo-provider",
        name = "Dubbo 服务提供者",
        order = 12,
        configType = DubboProviderConfig.class,
        allowBodyTypes = {BodyType.OBJECT},
        inputSerializeType = BodySerializeType.Object,
        outputSerializeType = BodySerializeType.Object
)
public class DalaranDubboProvider implements DalaranTrigger<DubboProviderConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?application=%s&registryAddress=%s&serviceId=%s&method=%s&version=%s";

    @Override
    public void buildFromRoute(RouteDefinition route, DubboProviderConfig config) {
        String uri = String.format(DUBBO_PROVIDER_URI, config.getConnector().getApplication(), config.getConnector().getAddress(),
                config.getServiceId(), config.getMethod(), config.getVersion());
        route.from(uri);
    }
}
