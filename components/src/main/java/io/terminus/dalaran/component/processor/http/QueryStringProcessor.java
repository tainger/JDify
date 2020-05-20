package io.terminus.dalaran.component.processor.http;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

public class QueryStringProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String queryString = buildQueryString(exchange.getIn().getBody());
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        exchange.getOut().setHeader(Exchange.HTTP_QUERY, queryString);
    }

    public String buildQueryString(Object obj) {
        if (obj instanceof Map) {
            Map queryKV = Maps.filterEntries((Map) obj, (Predicate<Map.Entry>) entry -> entry.getValue() != null && entry.getKey() != null);
            return Joiner.on("&").withKeyValueSeparator("=").join(queryKV);
        }
        return null;
    }
}
