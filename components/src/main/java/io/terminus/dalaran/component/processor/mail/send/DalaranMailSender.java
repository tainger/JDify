package io.terminus.dalaran.component.processor.mail.send;


import io.terminus.dalaran.component.connector.MailConnector;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

@Processor(
        value = "Mail-Sender",
        order = 23,
        configType = DalaranMailSenderConfig.class
)
public class DalaranMailSender implements DalaranProcessor<DalaranMailSenderConfig> {

    private static final String MAIL_ROUTE_URI = "%s://%s?username=%s&password=%s&to=%s&cc=%s&subject=%s&from=dalaran@terminus.com";

    @Override
    public void configure(ProcessorDefinition route, DalaranMailSenderConfig config) {
        MailConnector connector = config.getConnector();
        String uri = String.format(MAIL_ROUTE_URI, connector.getProtocol().name().toLowerCase(), connector.getHost(),
                connector.getUsername(), connector.getPassword(), config.getSendTo(), config.getCcTo(), config.getSubject());
        route.process(new DalaranMailSenderProcessor()).to(uri);
    }
}
