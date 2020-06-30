package io.terminus.dalaran.component.trigger.ftp;

import io.terminus.dalaran.component.connector.FtpConnector;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;

@Trigger(
        value = "ftp-download",
        order = 11,
        configType = FtpDownloadConfig.class,
        bodyType = "CSV"
)
public class FtpDownload implements DalaranTrigger<FtpDownloadConfig> {

    private static final String FTP_ROUTE_URI = "%s:%s:%s/%s?passiveMode=true&delete=%s&scheduler=quartz2&scheduler.cron=%s";

    @Override
    public void buildFromRoute(RouteDefinition route, FtpDownloadConfig config) {
        FtpConnector connector = config.getConnector();
        String uri = String.format(FTP_ROUTE_URI, connector.getProtocol().toString().toLowerCase(), connector.getHost(),
                connector.getPort(), config.getPath(), config.getDelete(), config.getCron());
        if (StringUtils.isNotBlank(connector.getUsername())) {
            uri += "&username=" + connector.getUsername();
        }
        if (StringUtils.isNotBlank(connector.getPassword())) {
            uri += "&password=" + connector.getPassword();
        }
        if (connector.getTimeout() != null) {
            uri += "&timeout=" + connector.getTimeout();
        }
        if (config.isFuzzyMatching()) {
            uri += "&include=" + config.getFileName();
        } else {
            uri += "&fileName=" + config.getFileName();
        }
        route.from(uri);
    }
}
