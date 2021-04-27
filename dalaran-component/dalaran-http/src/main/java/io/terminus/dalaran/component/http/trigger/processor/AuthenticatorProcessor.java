package io.terminus.dalaran.component.http.trigger.processor;

import io.terminus.dalaran.component.dynamic.DynamicAuthenticatorDefault;
import io.terminus.dalaran.component.authenticator.DalaranAuthenticator;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static io.terminus.dalaran.component.http.trigger.utils.SignUtils.stopExchangeOnInvalidAppKey;

@Slf4j
public class AuthenticatorProcessor implements Processor {

    private DalaranAuthenticator authenticator;

    private RedisService redisService;

    public AuthenticatorProcessor(DalaranAuthenticator authenticator, RedisService redisService) {
        this.authenticator = authenticator;
        this.redisService = redisService;
    }

    @Override
    public void process(Exchange exchange) {
        Map<String, String> body = exchange.getIn().getBody(Map.class);
        if (authenticator.getType().equals("Default")) {
            checkValue(exchange, body);
        }
    }

    void checkValue(Exchange exchange, Map<String, String> body) {
        List<DynamicAuthenticatorDefault> dynamicAuthenticatorDefaults = authenticator.getConfig();
        dynamicAuthenticatorDefaults.forEach(dynamicAuthenticatorDefault -> {
            String value = dynamicAuthenticatorDefault.getAuthenticatorValue();
            String requestValue;
            if (!dynamicAuthenticatorDefault.getIsStatic()) {
                value = redisService.getValue("Authenticator-" + dynamicAuthenticatorDefault.getAuthenticatorKey());
                if (StringUtils.isBlank(value)) {
                    stopExchangeOnInvalidAppKey(exchange);
                    return;
                }
            }
            if (StringUtils.equals(dynamicAuthenticatorDefault.getKeyLocation().name(), AuthenticatorKeyLocation.Header.name())) {
                requestValue = exchange.getIn().getHeader(dynamicAuthenticatorDefault.getAuthenticatorKey(), String.class);
            } else if (dynamicAuthenticatorDefault.getKeyLocation() == AuthenticatorKeyLocation.QueryParam) {
                requestValue = exchange.getIn().getHeader(dynamicAuthenticatorDefault.getAuthenticatorKey(), String.class);
            } else {
                if (body != null) {
                    requestValue = body.get(dynamicAuthenticatorDefault.getAuthenticatorKey());
                } else {
                    requestValue = "";
                }
            }
            if (StringUtils.isBlank(requestValue) || !requestValue.equals(value)) {
                stopExchangeOnInvalidAppKey(exchange);
            }
        });
        exchange.getOut().setBody(body);
    }

    void checkGetValue(Exchange exchange, Map<String, String> param) {
        if (authenticator.getType().equals("Default")) {
            List<DynamicAuthenticatorDefault> dynamicAuthenticatorDefaults = authenticator.getConfig();
            dynamicAuthenticatorDefaults.forEach(dynamicAuthenticatorDefault -> {
                String value = dynamicAuthenticatorDefault.getAuthenticatorValue();
                String requestValue;
                if (!dynamicAuthenticatorDefault.getIsStatic()) {
                    value = redisService.getValue("Authenticator-" + dynamicAuthenticatorDefault.getAuthenticatorKey());
                    if (StringUtils.isBlank(value)) {
                        stopExchangeOnInvalidAppKey(exchange);
                        return;
                    }
                }
                if (dynamicAuthenticatorDefault.getKeyLocation() == AuthenticatorKeyLocation.Header) {
                    requestValue = exchange.getIn().getHeader(dynamicAuthenticatorDefault.getAuthenticatorKey(), String.class);
                } else if (dynamicAuthenticatorDefault.getKeyLocation() == AuthenticatorKeyLocation.Body) {
                    Map<String, String> body = exchange.getIn().getBody(Map.class);
                    requestValue = body.get(dynamicAuthenticatorDefault.getAuthenticatorKey());
                } else {
                    if (param != null) {
                        requestValue = param.get(dynamicAuthenticatorDefault.getAuthenticatorKey());
                    } else {
                        requestValue = "";
                    }
                }
                if (StringUtils.isBlank(requestValue) || !requestValue.equals(value)) {
                    stopExchangeOnInvalidAppKey(exchange);
                }
            });
            exchange.getOut().setBody(param);
        }
    }
}
