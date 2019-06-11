package io.terminus.dalaran.component.processor.soap;

import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.core.component.DalaranProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/5/23
 */
public class DalaranSoapClient implements DalaranProcessor<SoapClientConfig> {

    private static final String HTTP_URI = "%s4://%s:%s%s";

    @Override
    public void configure(ProcessorDefinition route, SoapClientConfig config) {
        String uri = String.format(HTTP_URI, config.getConnector().getProtocol().name().toLowerCase(),
                config.getConnector().getHost(), config.getConnector().getPort(), config.getPath());
        route.setHeader(Exchange.HTTP_METHOD, Builder.constant(config.getMethod().name()));
        route.setHeader(Exchange.CONTENT_TYPE, Builder.constant("text/xml"));
        route.to(uri);
        // TODO Stream to string
        route.convertBodyTo(String.class);
    }
}
