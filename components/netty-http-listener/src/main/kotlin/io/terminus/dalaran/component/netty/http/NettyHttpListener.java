package io.terminus.dalaran.component.netty.http;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.DalaranComponent;
import org.apache.camel.model.RouteDefinition;

@DalaranComponent(value = "netty-http-listener", configType = NettyHttpConfig.class, bodyMode = BodyMode.Serialized)
public class NettyHttpListener implements DalaranTrigger<NettyHttpConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, NettyHttpConfig config) {
        String uri = "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://" + config.getHost() + ":" + config.getPort() + config.getPath() +
                "?httpMethodRestrict=" + config.getMethod();
        route.from(uri);
    }
}
