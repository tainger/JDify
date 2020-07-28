package io.terminus.dalaran.component.processor.ftp.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
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
        String fileName;
        if (config.isDynamicFileName()) {
            Object in = exchange.getIn().getBody();
            JSON body;
            if (in instanceof String) {
                body = JSON.parseObject((String)in, JSON.class);
            } else if (in instanceof byte[]) {
                body = JSON.parseObject(IOUtils.toString((byte[])in), JSON.class);
            } else {
                body = JSON.parseObject(JSON.toJSONString(in), JSON.class);
            }
            if (!JSONPath.contains(body, config.getDynamicPath())) {
                throw new RuntimeException("body: " + body + ", no file name");
            }
            fileName = JSONPath.eval(body, config.getDynamicPath()).toString();

        } else {
            fileName = config.getFileRoot();
            if (StringUtils.isNotBlank(config.getDatePattern())) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(config.getDatePattern());
                fileName += "." +  dateFormat.format(new Date());
            }
            if (StringUtils.isNotBlank(config.getFileSuffix())) {
                fileName += "." + config.getFileSuffix();
            }
        }
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        exchange.getOut().setHeader("CamelOverruleFileName", fileName);
        exchange.getOut().setBody(exchange.getIn().getBody());
    }
}
