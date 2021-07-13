package io.terminus.dalaran.component.mail.camel;

import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.connector.MailConnector;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.oss.OSSAccount;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Processor(
        value = "Mail-Sender",
        order = 23,
        configType = DalaranMailSenderConfig.class,
        developer = DalaranConstants.DALARAN
)
public class CamelMailSender implements DalaranProcessor<DalaranMailSenderConfig> {

    @Autowired
    private OSSAccount ossAccount;

    private static final String MAIL_ROUTE_URI = "%s://%s:%s?username=%s&password=%s&cc=%s&subject=%s&from=%s&debugMode=true&mail.smtps.auth=true&mail.smtps.starttls.enable=true";

    @Override
    public void configure(ProcessorDefinition route, DalaranMailSenderConfig config) {
        MailConnector connector = config.getConnector();
        String uri = String.format(MAIL_ROUTE_URI, connector.getProtocol().name().toLowerCase(),
                connector.getHost(), connector.getPort(), connector.getUsername(), connector.getPassword(),
                config.getCcTo(), config.getSubject(), config.getConnector().getFrom());
        route.process(new CamelMailSenderProcessor(config, ossAccount));
        if (config.isDynamicAddress()) {
            route.toD(uri + "&to=${headers." + ComponentConstants.DALARAN_MAIL_TO + "}");
            return;
        }
        route.to(uri + "&to=" + config.getSendTo());
    }
}
