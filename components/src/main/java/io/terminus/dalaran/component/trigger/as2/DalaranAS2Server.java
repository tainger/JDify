package io.terminus.dalaran.component.trigger.as2;

import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.DalaranTriggerBuildAfterProcessor;
import io.terminus.dalaran.core.component.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;

@Trigger(
        value = "as2-server",
        order = 19,
        configType = AS2ServerConfig.class
)
public class DalaranAS2Server implements DalaranTrigger<AS2ServerConfig>, DalaranTriggerBuildAfterProcessor<AS2ServerConfig> {

    private static final String AS2_SERVER_URI = "as2://server/listen?requestUri=%s&serverPortNumber=%s&requestUriPattern=%s";

    @Override
    public void buildFromRoute(RouteDefinition route, AS2ServerConfig config) {
//        String uri = "netty4-http:" + "http" +
//                "://0.0.0.0:" + config.getPort() + config.getRequestUri() +
//                "?httpMethodRestrict=POST";
        String uri = String.format(AS2_SERVER_URI, config.getRequestUri(), config.getPort(), config.getUriPattern());
        route.from(uri);
        route.process(new AS2ServerDataProcessor());
    }

    @Override
    public void buildAfter(RouteDefinition route, AS2ServerConfig config) {
        route.process(new AS2ServerAfterProcessor());
    }
}
