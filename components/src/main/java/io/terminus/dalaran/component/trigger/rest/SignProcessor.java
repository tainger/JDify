package io.terminus.dalaran.component.trigger.rest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static io.terminus.dalaran.DalaranConstants.AUTH_APP_KEY;
import static io.terminus.dalaran.DalaranConstants.AUTH_SIGN;
import static io.terminus.dalaran.component.trigger.rest.SignUtils.*;

public class SignProcessor implements Processor {
    private Map<String, String> clientMapper;

    SignProcessor(Map<String, String> clientMapper) {
        this.clientMapper = clientMapper;
    }

    @Override
    public void process(Exchange exchange) {
        Map<String, String> body = exchange.getIn().getBody(Map.class);
        checkSign(exchange, body);
    }

    void checkSign(Exchange exchange, Map<String, String> body) {
        String appKey = body.get(AUTH_APP_KEY);
        if (StringUtils.isEmpty(appKey)) {
            stopExchangeOnMissingAppKey(exchange);
            return;
        }
        String sign = body.get(AUTH_SIGN);
        if (StringUtils.isEmpty(sign)) {
            stopExchangeOnMissingSign(exchange);
            return;
        }
        String secret = clientMapper.get(appKey);
        if (StringUtils.isEmpty(secret)) {
            stopExchangeOnInvalidAppKey(exchange);
            return;
        }

        Map<String, String> sortedBody = new TreeMap<>(body);
        sortedBody.remove(AUTH_SIGN);

        String bodyString = sortedBody.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&")) + secret;

        if (SignUtils.signEquals(bodyString, sign)) {
            setOutBody(exchange, body);
            return;
        }
        stopExchangeOnInvalidSign(exchange);
    }

    void setOutBody(Exchange exchange, Map<String, String> body) {
        exchange.getOut().setBody(body.get("data"));
    }
}
