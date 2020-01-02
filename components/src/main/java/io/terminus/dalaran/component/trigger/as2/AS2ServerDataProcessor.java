package io.terminus.dalaran.component.trigger.as2;

import com.alibaba.fastjson.JSON;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.HashMap;
import java.util.Map;

public class AS2ServerDataProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
//        BasicHttpContext httpContext = exchange.getIn().getBody(BasicHttpContext.class);
//        BasicHttpRequest request = (BasicHttpRequest) httpContext.getAttribute("http.request");
//        List<String> body = IOUtils.readLines(request.getEntity().getContent(), "utf-8");
//        Object body = IOUtils.toString((InputStream) exchange.getIn().getBody(), StandardCharsets.UTF_8);
        Object body = exchange.getIn();
        System.out.println(body.getClass().toGenericString());
        Map<String, Object> result = new HashMap<>();
        result.put("status", "received");
        result.put("body", body.toString());
        exchange.getOut().setBody(JSON.toJSON(result));
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
