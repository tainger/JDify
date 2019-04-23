package io.terminus.dalaran.component.trigger.http;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;

@Trigger(value = "netty-http-listener", configType = NettyHttpConfig.class, bodyMode = BodyMode.Serialized)
public class NettyHttpListener implements DalaranTrigger<NettyHttpConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, NettyHttpConfig config) {
        String uri = "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://0.0.0.0:" + config.getPort() + config.getPath() +
                "?httpMethodRestrict=" + config.getMethod();
        route.from(uri);
        route.convertBodyTo(String.class);
    }
}
