package io.terminus.dalaran.component.processor.ftp;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FTPUpLoadPreProcessor implements Processor {

    private FtpUploadConfig config;

    public FTPUpLoadPreProcessor(FtpUploadConfig config) {
        this.config = config;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat(config.getDatePattern());
        String fileName = config.getFileRoot() + "." + dateFormat.format(new Date());
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        exchange.getOut().setHeader("CamelOverruleFileName", fileName);
        exchange.getOut().setBody(exchange.getIn().getBody());
    }
}
