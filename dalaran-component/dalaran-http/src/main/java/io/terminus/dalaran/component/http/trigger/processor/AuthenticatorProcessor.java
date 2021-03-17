package io.terminus.dalaran.component.http.trigger.processor;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.authenticator.DalaranAuthenticator;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.terminus.dalaran.component.http.trigger.utils.SignUtils.stopExchangeOnInvalidAppKey;

@Slf4j
public class AuthenticatorProcessor implements Processor {

    @Autowired
    private RedisService redisService;

    private DalaranAuthenticator authenticator;

    public AuthenticatorProcessor(DalaranAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void process(Exchange exchange) {
        Map<String, String> body = exchange.getIn().getBody(Map.class);
        checkValue(exchange, body);
    }

    void checkValue(Exchange exchange, Map<String, String> body) {
        log.info("checkSign() - keyLocation: " + authenticator.getKeyLocation());
        log.info("checkSign() - key: " + authenticator.getAuthenticatorKey());
        log.info("checkSign() - value: " + authenticator.getAuthenticatorValue());
        if (!authenticator.isStatic()) {
            String redisValue = redisService.getValue("Authenticator-" + authenticator.getAuthenticatorKey());
            if (StringUtils.isBlank(redisValue)) {
                stopExchangeOnInvalidAppKey(exchange);
                return;
            }
        }
        String value;
        if (authenticator.getKeyLocation() == AuthenticatorKeyLocation.Header) {
            value = exchange.getIn().getHeader(authenticator.getAuthenticatorKey(), String.class);
        } else {
            value = body.get(authenticator.getAuthenticatorKey());
            body.remove(authenticator.getAuthenticatorKey());
        }
        if (StringUtils.isBlank(value) || !value.equals(authenticator.getAuthenticatorValue())) {
            stopExchangeOnInvalidAppKey(exchange);
            return;
        }
        exchange.getOut().setBody(body);
    }
}
