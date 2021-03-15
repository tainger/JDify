package io.terminus.dalaran.component.http.trigger.processor;

import io.terminus.dalaran.component.authenticator.DalaranAuthenticator;
import io.terminus.dalaran.component.basic.BasicAuthenticator;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static io.terminus.dalaran.component.http.trigger.utils.SignUtils.stopExchangeOnInvalidAppKey;

@Slf4j
public class AuthenticatorProcessor implements Processor {

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
