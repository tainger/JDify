package io.terminus.dalaran.component.trigger.rest.processor;

import io.terminus.dalaran.component.trigger.rest.utils.SignUtils;
import lombok.Data;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static io.terminus.dalaran.DalaranConstants.*;
import static io.terminus.dalaran.component.trigger.rest.utils.SignUtils.*;

@Data
public class SignProcessor implements Processor {

    private Boolean checkSign;

    private Map<String, String> clientMapper;

    public SignProcessor(Map<String, String> clientMapper, Boolean checkSign) {
        this.clientMapper = clientMapper;
        this.checkSign = checkSign;
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
        String secret = clientMapper.get(appKey);
        if (StringUtils.isEmpty(secret) || !secret.equals(body.get(AUTH_APP_SECRET))) {
            stopExchangeOnInvalidAppKey(exchange);
            return;
        }

        String sign = body.get(AUTH_SIGN);
        if (checkSign && !StringUtils.isEmpty(sign)) {
            Map<String, String> sortedBody = new TreeMap<>(body);
            sortedBody.remove(AUTH_SIGN);

            String bodyString = sortedBody.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&")) + secret;

            if (SignUtils.signEquals(bodyString, sign)) {
                setOutBody(exchange, body);
                return;
            }
            stopExchangeOnMissingSign(exchange);
            return;
        }
        setOutBody(exchange, body);
    }

    void setOutBody(Exchange exchange, Map<String, String> body) {
        body.remove(AUTH_SIGN);
        body.remove(AUTH_APP_SECRET);
        body.remove(AUTH_APP_KEY);
        exchange.getOut().setBody(body);
    }
}
