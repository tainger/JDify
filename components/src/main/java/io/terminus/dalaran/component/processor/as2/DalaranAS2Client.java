package io.terminus.dalaran.component.processor.as2;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.builder.Builder;
import org.apache.camel.component.as2.api.AS2MediaType;
import org.apache.camel.component.as2.api.AS2MessageStructure;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.http.entity.ContentType;

import java.nio.charset.StandardCharsets;

@Processor(
        value = "as2-client",
        order = 18,
        configType = AS2ClientConfig.class,
        bodyType = "JSON"
)
public class DalaranAS2Client implements DalaranProcessor<AS2ClientConfig> {

    private static final String AS2_CLIENT_URI = "as2://client/send?inBody=%s&targetHostname=%s&requestUri=%s&targetPortNumber=%s";

    @Override
    public void configure(ProcessorDefinition route, AS2ClientConfig config) {
        String uri = String.format(AS2_CLIENT_URI, config.getBodyType(), config.getConnector().getHost(), config.getRequestUri(), config.getConnector().getPort());
        route.setHeader("CamelAS2.ediMessageContentType", Builder.constant(ContentType.create(AS2MediaType.APPLICATION_EDIFACT, StandardCharsets.UTF_8)));
        route.setHeader("CamelAS2.ediMessageTransferEncoding", Builder.constant("7bit"));
        route.setHeader("CamelAS2.requestUri", Builder.constant(config.getRequestUri()));
        route.setHeader("CamelAS2.from", Builder.constant("mrAS@example.org"));
        route.setHeader("CamelAS2.as2From", Builder.constant("878051556"));
        route.setHeader("CamelAS2.as2To", Builder.constant("878051556"));
        route.setHeader("CamelAS2.dispositionNotificationTo", Builder.constant("mrAS@example.org"));
        route.setHeader("CamelAS2.subject", Builder.constant("Signed AS2 Message Example"));
        route.setHeader("CamelAS2.as2MessageStructure", Builder.constant(AS2MessageStructure.PLAIN));
        route.process(new AS2ClientPreProcessor(config));
        route.to(uri);
//        route.process(new AS2ClientDataProcessor());
    }
}
