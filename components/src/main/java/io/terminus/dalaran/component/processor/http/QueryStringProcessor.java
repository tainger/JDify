package io.terminus.dalaran.component.processor.http;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;

import java.util.Map;

public class QueryStringProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String queryString = buildQueryString(exchange.getIn().getBody());
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        exchange.getOut().setHeader(Exchange.HTTP_QUERY, queryString);
    }

    private String buildQueryString(Object obj) throws Exception {
        Map inBody;
        if (obj instanceof byte[]) {
            inBody = JSON.parseObject(IOUtils.toString((byte[]) obj), Map.class);
        } else if (obj instanceof String) {
            inBody = JSON.parseObject((String)obj, Map.class);
        } else {
            inBody = JSON.parseObject(JSON.toJSONString(obj), Map.class);
        }
        Map queryKV = Maps.filterEntries(inBody, (Predicate<Map.Entry>) entry -> entry.getValue() != null && entry.getKey() != null);
        return Joiner.on("&").withKeyValueSeparator("=").join(queryKV);
    }
}
