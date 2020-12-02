package io.terminus.dalaran.component.ftp.processor.custom;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import freemarker.template.Template;
import io.terminus.dalaran.component.ftp.processor.FtpInputType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.DALARAN_CONTEXT_EXCHANGE;

public class CustomFTPUpLoadPreProcessor implements Processor {

    private CustomFtpUploadConfig config;

    private Template template;

    public CustomFTPUpLoadPreProcessor(CustomFtpUploadConfig config, Template template) {
        this.config = config;
        this.template = template;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String fileName;
        if (config.isDynamicFileName()) {
            Map<String, Object> contextValues = (Map<String, Object>)exchange.getProperties().get(DALARAN_CONTEXT_EXCHANGE + exchange.getExchangeId());
            if (config.getInputType() == FtpInputType.XML || (config.getInputType() == FtpInputType.ARRAY && contextValues.containsKey(config.getDynamicPath()))) {
                fileName = contextValues.get(config.getDynamicPath()).toString();
            } else {
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
                    if (contextValues.containsKey(config.getDynamicPath())) {
                        fileName = contextValues.get(config.getDynamicPath()).toString();
                    } else {
                        throw new RuntimeException("body: " + body + ", no file name");
                    }
                } else {
                    fileName = JSONPath.eval(body, config.getDynamicPath()).toString();
                }
            }
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
