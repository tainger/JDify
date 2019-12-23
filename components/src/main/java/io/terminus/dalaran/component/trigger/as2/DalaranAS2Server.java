package io.terminus.dalaran.component.trigger.as2;

import io.terminus.dalaran.core.component.DalaranTrigger;
import org.apache.camel.model.RouteDefinition;

public class DalaranAS2Server implements DalaranTrigger<AS2ServerConfig> {

    private static final String AS2_SERVER_URI = "as2://server/listen?requestUri=%s&requestUriPattern=%s&serverPortNumber=%s";

    @Override
    public void buildFromRoute(RouteDefinition route, AS2ServerConfig config) {
        String uri = String.format(AS2_SERVER_URI, config.getConnector().getHost(), config.getUriPattern(), config.getConnector().getPort());
        route.from(uri);
    }
}
