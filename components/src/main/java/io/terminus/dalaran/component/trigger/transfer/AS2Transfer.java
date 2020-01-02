package io.terminus.dalaran.component.trigger.transfer;

import io.terminus.dalaran.component.trigger.as2.AS2ServerConfig;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.DalaranTriggerBuildAfterProcessor;
import io.terminus.dalaran.core.component.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;

@Trigger(
        value = "as2-transfer",
        order = 21,
        configType = AS2ServerConfig.class
)
public class AS2Transfer implements DalaranTrigger<AS2ServerConfig>, DalaranTriggerBuildAfterProcessor<AS2ServerConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, AS2ServerConfig config) {
        String uri = "netty4-http:" + "http" +
                "://0.0.0.0:" + config.getPort() + config.getRequestUri() +
                "?httpMethodRestrict=POST";
        route.from(uri);
        route.process(new AS2TransferProcessor());
    }

    @Override
    public void buildAfter(RouteDefinition route, AS2ServerConfig config) {
        route.process(new AS2TransferAfterProcessor());
    }
}
