package io.terminus.dalaran.component.processor.as2;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.BasicHttpContext;

import java.util.List;

public class AS2ClientDataProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        BasicHttpContext httpContext = exchange.getIn().getBody(BasicHttpContext.class);
        BasicHttpEntityEnclosingRequest request = (BasicHttpEntityEnclosingRequest) httpContext.getAttribute("http.request");
        List<String> body = IOUtils.readLines(request.getEntity().getContent(), "utf-8");
        exchange.getOut().setBody(body);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
