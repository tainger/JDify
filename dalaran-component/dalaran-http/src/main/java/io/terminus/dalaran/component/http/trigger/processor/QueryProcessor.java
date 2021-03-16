package io.terminus.dalaran.component.http.trigger.processor;


import com.google.common.base.Splitter;
import io.terminus.dalaran.component.authenticator.DalaranAuthenticator;
import io.terminus.dalaran.component.basic.BasicAuthenticator;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static io.terminus.dalaran.component.http.trigger.utils.SignUtils.stopExchangeOnMissingAppKey;

public class QueryProcessor extends AuthenticatorProcessor {

    public QueryProcessor(DalaranAuthenticator authenticator) {
        super(authenticator);
    }

    @Override
    public void process(Exchange exchange) {
        String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        if (StringUtils.isEmpty(queryString)) {
            stopExchangeOnMissingAppKey(exchange);
            return;
        }
        Map<String, String> body = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
        super.checkValue(exchange, body);
    }

}
