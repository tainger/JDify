package io.terminus.dalaran.component.netty.http;

import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.DalaranComponent;

@DalaranComponent(value = "netty-http-listener", configType = NettyHttpConfig.class)
public class NettyHttpListener implements DalaranTrigger<NettyHttpConfig> {

    @Override
    public String buildRouterUri(NettyHttpConfig config) {
        return "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://" +
                config.getHost() +
                ":" +
                config.getPort() +
                config.getPath() +
                "?httpMethodRestrict=" +
                config.getMethod();
    }
}
