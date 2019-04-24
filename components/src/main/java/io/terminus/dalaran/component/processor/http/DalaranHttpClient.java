package io.terminus.dalaran.component.processor.http;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;

@Processor(value = "http-client", configType = HttpClientConfig.class, bodyMode = BodyMode.Serialized)
public class DalaranHttpClient implements DalaranProcessor<HttpClientConfig> {
    private static final String HTTP_URI = "%s4://%s:%s%s?bridgeEndpoint=true";

    // TODO form && queryString
    @Override
    public void configure(ProcessorDefinition route, HttpClientConfig config) {
        String uri = String.format(HTTP_URI, config.getProtocol().name().toLowerCase(), config.getHost(), config.getPort(), config.getPath());
        route.setHeader("CamelHttpMethod", Builder.constant(config.getMethod().name()));
//        route.setHeader(Exchange.HTTP_QUERY, simple("?b=${in.header.b}"));
        route.to(uri);
        // TODO Stream to string
        route.convertBodyTo(String.class);
    }
}
