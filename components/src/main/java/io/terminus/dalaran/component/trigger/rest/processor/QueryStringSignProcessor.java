package io.terminus.dalaran.component.trigger.rest.processor;

import com.google.common.base.Splitter;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static io.terminus.dalaran.component.http.trigger.utils.SignUtils.stopExchangeOnMissingAppKey;

public class QueryStringSignProcessor extends SignProcessor {


    public QueryStringSignProcessor(Map<String, String> clientMapper, Boolean checkSign) {
        super(clientMapper, checkSign);
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
