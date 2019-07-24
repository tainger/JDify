package io.terminus.dalaran.component.trigger.http;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.model.BodyType;
import org.apache.camel.model.RouteDefinition;

@Trigger(
        value = "netty-http-listener",
        name = "Http 监听器",
        configType = NettyHttpConfig.class,
        allowBodyTypes = {BodyType.JSON, BodyType.XML},
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
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
