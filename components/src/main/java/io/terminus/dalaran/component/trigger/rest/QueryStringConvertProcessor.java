package io.terminus.dalaran.component.trigger.rest;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class QueryStringConvertProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        if (StringUtils.isNotEmpty(queryString)) {
            Map body = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
            exchange.getOut().setBody(body);
        } else {
            exchange.getOut().setBody(Maps.newHashMap());
        }
    }
}
