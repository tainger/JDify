package io.terminus.dalaran.component.http.trigger.processor;


import com.google.common.base.Splitter;
import io.terminus.dalaran.component.authenticator.AuthenticatorConfigType;
import io.terminus.dalaran.core.resource.redis.RedisService;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;


public class QueryProcessor extends AuthenticatorProcessor {

    public QueryProcessor(AuthenticatorConfigType authenticator, RedisService redisService) {
        super(authenticator, redisService);
    }

    @Override
    public void process(Exchange exchange) {
        String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        Map<String, String> body;
        if (StringUtils.isEmpty(queryString)) {
            body = null;
        } else {
            body = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
        }
        super.checkGetValue(exchange, body);
    }

}
