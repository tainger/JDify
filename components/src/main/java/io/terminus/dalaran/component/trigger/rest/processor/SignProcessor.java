package io.terminus.dalaran.component.trigger.rest.processor;

import lombok.Data;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.AUTH_APP_KEY;
import static io.terminus.dalaran.DalaranConstants.AUTH_APP_SECRET;
import static io.terminus.dalaran.component.trigger.rest.utils.SignUtils.stopExchangeOnInvalidAppKey;
import static io.terminus.dalaran.component.trigger.rest.utils.SignUtils.stopExchangeOnMissingAppKey;

@Data
public class SignProcessor implements Processor {
    private Map<String, String> clientMapper;

    public SignProcessor(Map<String, String> clientMapper) {
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
//        String sign = body.get(AUTH_SIGN);
//        if (StringUtils.isEmpty(sign)) {
//            stopExchangeOnMissingSign(exchange);
//            return;
//        }
        String secret = clientMapper.get(appKey);
        if (StringUtils.isEmpty(secret) || !secret.equals(body.get(AUTH_APP_SECRET))) {
            stopExchangeOnInvalidAppKey(exchange);
            return;
        }
        setOutBody(exchange, body);

//        Map<String, String> sortedBody = new TreeMap<>(body);
//        sortedBody.remove(AUTH_SIGN);

//        String bodyString = sortedBody.entrySet().stream()
//                .map(entry -> entry.getKey() + "=" + entry.getValue())
//                .collect(Collectors.joining("&")) + secret;

//        if (SignUtils.signEquals(bodyString, sign)) {
//            setOutBody(exchange, body);
//            return;
//        }
//        stopExchangeOnInvalidSign(exchange);
    }

    void setOutBody(Exchange exchange, Map<String, String> body) {
        exchange.getOut().setBody(body.get("data"));
    }
}
