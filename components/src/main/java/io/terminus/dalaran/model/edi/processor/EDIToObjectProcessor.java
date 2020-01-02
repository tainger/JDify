package io.terminus.dalaran.model.edi.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.BasicHttpContext;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EDIToObjectProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object in = exchange.getIn().getBody();
        Object body;
        if (in instanceof BasicHttpContext) {
            BasicHttpContext httpContext = exchange.getIn().getBody(BasicHttpContext.class);
            BasicHttpEntityEnclosingRequest request = (BasicHttpEntityEnclosingRequest) httpContext.getAttribute("http.request");
            body = IOUtils.readLines(request.getEntity().getContent(), "utf-8");
        } else {
            body = IOUtils.toString((InputStream)in, StandardCharsets.UTF_8);
        }
        exchange.getOut().setBody(body);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
