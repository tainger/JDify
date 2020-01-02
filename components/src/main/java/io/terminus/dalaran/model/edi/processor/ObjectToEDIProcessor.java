package io.terminus.dalaran.model.edi.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;

public class ObjectToEDIProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object inBody = exchange.getIn().getBody();
        if (!(inBody instanceof byte[])) {
            byte[] bytes = IOUtils.toByteArray(JSON.toJSONString(JSONPath.eval(inBody, "$.data")));
            File temp = File.createTempFile("dalaran-edi-" + System.currentTimeMillis(), ".edi");
            FileUtils.writeByteArrayToFile(temp, bytes);
            exchange.getOut().setBody(new FileInputStream(temp));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        } else {
            byte[] bytes = IOUtils.toByteArray(JSON.toJSONString(JSONPath.eval(JSON.parseObject(IOUtils.toString((byte[])inBody, "utf-8")), "$.data")));
            File temp = File.createTempFile("dalaran-edi-" + System.currentTimeMillis(), ".edi");
            FileUtils.writeByteArrayToFile(temp, bytes);
            exchange.getOut().setBody(new FileInputStream(temp));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }
}
