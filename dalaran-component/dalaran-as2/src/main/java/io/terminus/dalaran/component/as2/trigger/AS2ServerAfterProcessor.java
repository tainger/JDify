package io.terminus.dalaran.component.as2.trigger;

import com.alibaba.fastjson.JSON;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
//import org.apache.camel.component.as2.api.AS2MediaType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;

public class AS2ServerAfterProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "APPLICATION_EDIFACT");
        Object inBody = exchange.getIn().getBody();
        if (!(inBody instanceof byte[])) {
            byte[] bytes = IOUtils.toByteArray(JSON.toJSONString(inBody));
            File temp = File.createTempFile("dalaran-server-" + System.currentTimeMillis(), ".edi");
            FileUtils.writeByteArrayToFile(temp, bytes);
            exchange.getOut().setBody(new FileInputStream(temp));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        } else {
            byte[] bytes = IOUtils.toByteArray(IOUtils.toString((byte[])inBody, "utf-8"));
            File temp = File.createTempFile("dalaran-server-" + System.currentTimeMillis(), ".edi");
            FileUtils.writeByteArrayToFile(temp, bytes);
            exchange.getOut().setBody(new FileInputStream(temp));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }
}
