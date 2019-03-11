package io.terminus.dalaran.component.http.request;

import io.terminus.dalaran.annotation.DalaranComponent;
import io.terminus.dalaran.DalaranProcessor;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.RouteDefinition;

@DalaranComponent(value = "http-client", configType = HttpClientConfig.class)
public class DalaranHttpClient implements DalaranProcessor<HttpClientConfig> {
    private static final String HTTP_URI = "%s4://%s:%s%s?bridgeEndpoint=true";

    @Override
    public void configure(RouteDefinition route, HttpClientConfig config) {
        String uri = String.format(HTTP_URI, config.getProtocol().getValue(), config.getHost(), config.getPort(), config.getPath());
        route.setHeader("CamelHttpMethod", Builder.constant(config.getMethod())).to(uri);
    }
}
