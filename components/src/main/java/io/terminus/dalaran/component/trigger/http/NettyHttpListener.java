package io.terminus.dalaran.component.trigger.http;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;

@Trigger(value = "netty-http-listener", configType = NettyHttpConfig.class, allowBodyTypes = {BodyType.JSON, BodyType.XML}, serializedBody = true)
public class NettyHttpListener implements DalaranTrigger<NettyHttpConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, NettyHttpConfig config) {
        String uri = "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://0.0.0.0:" + config.getPort() + config.getPath() +
                "?httpMethodRestrict=" + config.getMethod();
        route.from(uri);
        if (config.getMethod().isNoBody()) {
            route.process(new QueryStringProcessor());
        } else {
            // TODO Stream to string
            route.convertBodyTo(String.class);
        }
    }
}
