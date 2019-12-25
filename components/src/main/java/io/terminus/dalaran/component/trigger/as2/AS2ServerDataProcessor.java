package io.terminus.dalaran.component.trigger.as2;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AS2ServerDataProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
//        BasicHttpContext httpContext = exchange.getIn().getBody(BasicHttpContext.class);
//        BasicHttpEntityEnclosingRequest request = (BasicHttpEntityEnclosingRequest) httpContext.getAttribute("http.request");
//        List<String> body = IOUtils.readLines(request.getEntity().getContent(), "utf-8");
        Object body = IOUtils.toString((InputStream) exchange.getIn().getBody(), StandardCharsets.UTF_8);
        exchange.getOut().setBody(body);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
