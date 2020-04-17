package io.terminus.dalaran.component.processor.carso.ftp;

import com.alibaba.fastjson.JSON;
import freemarker.template.Template;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarsoFTPUpLoadPreProcessor implements Processor {

    private CarsoFtpUploadConfig config;

    private Template template;

    public CarsoFTPUpLoadPreProcessor(CarsoFtpUploadConfig config, Template template) {
        this.config = config;
        this.template = template;
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
        exchange.getOut().setBody(transferBody(exchange.getIn().getBody()));
    }

    private Object transferBody(Object in) {
        Object body;
        if (in instanceof String) {
            body = JSON.parseObject((String)in);
        } else if (in instanceof byte[]) {
            body = JSON.parse((byte[])in);
        } else {
            body = in;
        }
        StringWriter out = new StringWriter();
        try {
            template.process(body, out);
            return out.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return in;
    }
}
