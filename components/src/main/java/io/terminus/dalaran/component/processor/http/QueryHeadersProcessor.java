package io.terminus.dalaran.component.processor.http;

import lombok.Data;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public class QueryHeadersProcessor implements Processor {

    private String headers;

    public QueryHeadersProcessor(String headers) {
        this.headers = headers;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String contextKey = "DalaranContextExchange" + exchange.getExchangeId();
        Map<String, Object> contextValues = (Map)exchange.getProperties().get(contextKey);
        Map<String, Object> headerValues = new HashMap<>();
        String[] headerNames = StringUtils.split(StringUtils.replaceChars(headers, " ", ""), ",");
        for (String name: headerNames) {
            headerValues.put(name, contextValues.get(name));
        }
        exchange.getOut().setBody(exchange.getIn().getBody());
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        headerValues.forEach((k, v) -> exchange.getOut().setHeader(k, v));
    }
}
