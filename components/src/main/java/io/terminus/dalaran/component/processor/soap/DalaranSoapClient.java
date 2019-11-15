package io.terminus.dalaran.component.processor.soap;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by jingdi on 2019/5/23
 */
@Processor(
        value = "soap-client",
        order = 11,
        configType = SoapClientConfig.class,
        bodyType = "SOAP"
)
public class DalaranSoapClient implements DalaranProcessor<SoapClientConfig> {

    private static final String HTTP_URI = "%s4://%s:%s%s?bridgeEndpoint=true";

    private static final String PATH_SPLIT = "?";

    @Override
    public void configure(ProcessorDefinition route, SoapClientConfig config) {
        String path = config.getPath();
        String params = "";
        if (config.getPath().contains(PATH_SPLIT)) {
            path = StringUtils.substringBefore(config.getPath(), PATH_SPLIT);
            params = StringUtils.substringAfter(config.getPath(), PATH_SPLIT);
        }
        String uri = String.format(HTTP_URI, config.getConnector().getProtocol().name().toLowerCase(),
                config.getConnector().getHost(), config.getConnector().getPort(), path);
        if (StringUtils.isNotBlank(params)) {
            uri = uri + "&" + params;
        }
        if (config.getConnector().getUsername() != null && config.getConnector().getPassword() != null) {
            uri = uri + "&authMethod=Basic&authUsername=" + config.getConnector().getUsername() + "&authPassword=" + config.getConnector().getPassword();
        }
        route.setHeader(Exchange.HTTP_METHOD, Builder.constant(config.getMethod().name()));
        route.setHeader(Exchange.CONTENT_TYPE, Builder.constant("text/xml"));
        route.to(uri);
        // TODO Stream to string
        route.convertBodyTo(String.class);
    }
}
