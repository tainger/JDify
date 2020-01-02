package io.terminus.dalaran.component.trigger.transfer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AS2TransferProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
//        BasicHttpContext httpContext = exchange.getIn().getBody(BasicHttpContext.class);
//        BasicHttpEntityEnclosingRequest request = (BasicHttpEntityEnclosingRequest) httpContext.getAttribute("http.request");
//        List<String> body = IOUtils.readLines(request.getEntity().getContent(), "utf-8");
        Object body = IOUtils.toString((InputStream) exchange.getIn().getBody(), StandardCharsets.UTF_8);
        Map<String, Object> input = new HashMap<>();
        input.put("data", body);
        exchange.getOut().setBody(input);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
