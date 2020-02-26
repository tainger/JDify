package io.terminus.dalaran.component.processor.ftp;

import io.terminus.dalaran.component.connector.FtpUploadConnector;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;

@Processor(
        value = "ftp-upload",
        order = 11,
        configType = FtpUploadConfig.class,
        bodyType = "CSV"
)
public class FtpUpload implements DalaranProcessor<FtpUploadConfig> {

    private static final String FTP_ROUTE_URI = "%s:%s:%s/%s?passiveMode=true&fileName=%s&fileExist=%s";

    @Override
    public void configure(ProcessorDefinition route, FtpUploadConfig config) {
        FtpUploadConnector connector = config.getConnector();
        String uri = String.format(FTP_ROUTE_URI, connector.getProtocol().toString().toLowerCase(), connector.getHost(),
                connector.getPort(), config.getPath(), config.getFileName(), config.getFileExist());
        if (StringUtils.isNotBlank(connector.getUsername())) {
            uri += "&username=" + connector.getUsername();
        }
        if (StringUtils.isNotBlank(connector.getPassword())) {
            uri += "&password=" + connector.getPassword();
        }
        if (connector.getTimeout() != null) {
            uri += "&timeout=" + connector.getTimeout();
        }
        route.to(uri).process(exchange -> {
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
            exchange.getOut().setBody(exchange.getIn().getBody());
        });
    }
}
