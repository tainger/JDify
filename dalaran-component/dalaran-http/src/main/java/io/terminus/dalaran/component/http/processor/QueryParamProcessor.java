package io.terminus.dalaran.component.http.processor;

import com.google.common.base.Joiner;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class QueryParamProcessor implements Processor {

    private String queryParams;

    public QueryParamProcessor(String queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String contextKey = "DalaranContextExchange" + exchange.getExchangeId();
        Map<String, Object> contextValues = (Map)exchange.getProperties().get(contextKey);
        Map<String, Object> queryValues = new HashMap<>();
        String[] paramNames = StringUtils.split(StringUtils.replaceChars(queryParams, " ", ""), ",");
        for (String name: paramNames) {
            queryValues.put(name, contextValues.get(name));
        }
        exchange.getOut().setBody(exchange.getIn().getBody());
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        String queryString = Joiner.on("&").withKeyValueSeparator("=").join(queryValues);
        exchange.getOut().setHeader(Exchange.HTTP_QUERY, queryString);
    }
}
