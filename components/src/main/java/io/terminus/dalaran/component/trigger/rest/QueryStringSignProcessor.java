package io.terminus.dalaran.component.trigger.rest;

import com.google.common.base.Splitter;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static io.terminus.dalaran.component.trigger.rest.SignUtils.stopExchangeOnMissingAppKey;

public class QueryStringSignProcessor extends SignProcessor {

    QueryStringSignProcessor(Map<String, String> clientMapper) {
        super(clientMapper);
    }

    @Override
    public void process(Exchange exchange) {
        String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        if (StringUtils.isEmpty(queryString)) {
            stopExchangeOnMissingAppKey(exchange);
            return;
        }
        Map<String, String> body = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
        super.checkSign(exchange, body);
    }

    @Override
    void setOutBody(Exchange exchange, Map<String, String> body) {
        exchange.getOut().setBody(body);
    }
}
