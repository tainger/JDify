package io.terminus.dalaran.component.trigger.mail.pull;

import io.terminus.dalaran.component.connector.MailConnector;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;

@Trigger(
        value = "Mail-Reader",
        order = 22,
        configType = DalaranMailReaderConfig.class
)
public class DalaranMailReader implements DalaranTrigger<DalaranMailReaderConfig> {

    private static final String MAIL_ROUTE_URI = "%s://%s?username=%s&password=%s&searchTerm.from=%s&scheduler=%s&delete=%s&unseen=true";

    @Override
    public void buildFromRoute(RouteDefinition route, DalaranMailReaderConfig config) {
        MailConnector connector = config.getConnector();
        String uri = String.format(MAIL_ROUTE_URI, connector.getProtocol().name().toLowerCase(), connector.getHost(),
                connector.getUsername(), connector.getPassword(), config.getReadFrom(), config.getScheduler(), config.getDelete());
        if (StringUtils.isNotBlank(config.getReadSubject())) {
            uri += "&searchTerm.subject=" + config.getReadSubject();
        }
        route.from(uri).process(new DalaranMailReaderProcessor());
    }
}
