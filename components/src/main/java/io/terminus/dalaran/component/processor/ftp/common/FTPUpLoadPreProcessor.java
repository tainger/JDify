package io.terminus.dalaran.component.processor.ftp.common;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FTPUpLoadPreProcessor implements Processor {

    private FtpUploadConfig config;

    public FTPUpLoadPreProcessor(FtpUploadConfig config) {
        this.config = config;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String fileName = config.getFileRoot();
        if (StringUtils.isNotBlank(config.getDatePattern())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(config.getDatePattern());
            fileName += "." +  dateFormat.format(new Date());
        }
        if (StringUtils.isNotBlank(config.getFileSuffix())) {
            fileName += "." + config.getFileSuffix();
        }
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        exchange.getOut().setHeader("CamelOverruleFileName", fileName);
        exchange.getOut().setBody(exchange.getIn().getBody());
    }
}
