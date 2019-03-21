package io.terminus.dalaran.component.http.client;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.DalaranComponent;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;

@DalaranComponent(value = "http-client", configType = HttpClientConfig.class, bodyMode = BodyMode.Serialized)
public class DalaranHttpClient implements DalaranProcessor<HttpClientConfig> {
    private static final String HTTP_URI = "%s4://%s:%s%s?bridgeEndpoint=true";

    @Override
    public void configure(ProcessorDefinition route, HttpClientConfig config) {
        String uri = String.format(HTTP_URI, config.getProtocol().name().toLowerCase(), config.getHost(), config.getPort(), config.getPath());
        route.setHeader("CamelHttpMethod", Builder.constant(config.getMethod().name())).to(uri);
    }
}
