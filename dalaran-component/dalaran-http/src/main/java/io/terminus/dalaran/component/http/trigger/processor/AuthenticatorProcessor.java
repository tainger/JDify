package io.terminus.dalaran.component.http.trigger.processor;

import io.terminus.dalaran.component.authenticator.AuthenticatorRestConfig;
import io.terminus.dalaran.component.authenticator.AuthenticatorType;
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
        List<AuthenticatorRestConfig> authenticatorRestConfigs = authenticator.getConfig();
        authenticatorRestConfigs.forEach(authenticatorRestConfig -> {
            String value = authenticatorRestConfig.getAuthenticatorValue();
            String requestValue;
            if (!authenticatorRestConfig.getIsStatic()) {
                value = redisService.getValue("Authenticator-" + authenticatorRestConfig.getAuthenticatorKey());
                if (StringUtils.isBlank(value)) {
                    stopExchangeOnInvalidAppKey(exchange);
                    return;
                }
            }
            if (StringUtils.equals(authenticatorRestConfig.getKeyLocation().name(), AuthenticatorKeyLocation.Header.name())) {
                requestValue = exchange.getIn().getHeader(authenticatorRestConfig.getAuthenticatorKey(), String.class);
            } else if (authenticatorRestConfig.getKeyLocation() == AuthenticatorKeyLocation.QueryParam) {
                requestValue = exchange.getIn().getHeader(authenticatorRestConfig.getAuthenticatorKey(), String.class);
            } else {
                if (body != null) {
                    requestValue = body.get(authenticatorRestConfig.getAuthenticatorKey());
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
            List<AuthenticatorRestConfig> authenticatorRestConfigs = authenticator.getConfig();
            authenticatorRestConfigs.forEach(authenticatorRestConfig -> {
                String value = authenticatorRestConfig.getAuthenticatorValue();
                String requestValue;
                if (!authenticatorRestConfig.getIsStatic()) {
                    value = redisService.getValue("Authenticator-" + authenticatorRestConfig.getAuthenticatorKey());
                    if (StringUtils.isBlank(value)) {
                        stopExchangeOnInvalidAppKey(exchange);
                        return;
                    }
                }
                if (authenticatorRestConfig.getKeyLocation() == AuthenticatorKeyLocation.Header) {
                    requestValue = exchange.getIn().getHeader(authenticatorRestConfig.getAuthenticatorKey(), String.class);
                } else if (authenticatorRestConfig.getKeyLocation() == AuthenticatorKeyLocation.Body) {
                    Map<String, String> body = exchange.getIn().getBody(Map.class);
                    requestValue = body.get(authenticatorRestConfig.getAuthenticatorKey());
                } else {
                    if (param != null) {
                        requestValue = param.get(authenticatorRestConfig.getAuthenticatorKey());
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
