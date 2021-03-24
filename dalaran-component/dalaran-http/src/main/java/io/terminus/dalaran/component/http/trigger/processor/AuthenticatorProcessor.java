package io.terminus.dalaran.component.http.trigger.processor;

import io.terminus.dalaran.component.authenticator.AuthenticatorRestConfig;
import io.terminus.dalaran.component.authenticator.DalaranAuthenticator;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
        checkValue(exchange, body);
    }

    void checkValue(Exchange exchange, Map<String, String> body) {
        List<AuthenticatorRestConfig> authenticatorRestConfigs = authenticator.getConfig();
        authenticatorRestConfigs.forEach(authenticatorRestConfig -> {
            if (!authenticatorRestConfig.getIsStatic()) {
                String redisValue = redisService.getValue("Authenticator-" + authenticatorRestConfig.getAuthenticatorKey());
                if (StringUtils.isBlank(redisValue)) {
                    stopExchangeOnInvalidAppKey(exchange);
                    return;
                }
            }
            String value;
            if (authenticatorRestConfig.getKeyLocation() == AuthenticatorKeyLocation.Header) {
                value = exchange.getIn().getHeader(authenticatorRestConfig.getAuthenticatorKey(), String.class);
            } else if (authenticatorRestConfig.getKeyLocation() == AuthenticatorKeyLocation.QueryParam) {
                value = exchange.getIn().getHeader(authenticatorRestConfig.getAuthenticatorKey(), String.class);
            } else {
                if (body != null) {
                    value = body.get(authenticatorRestConfig.getAuthenticatorKey());
                } else {
                    value = "";
                }
            }
            if (StringUtils.isBlank(value) || !value.equals(authenticatorRestConfig.getAuthenticatorValue())) {
                stopExchangeOnInvalidAppKey(exchange);
            }
        });
        exchange.getOut().setBody(body);
    }

    void checkGetValue(Exchange exchange, Map<String, String> param) {
        List<AuthenticatorRestConfig> authenticatorRestConfigs = authenticator.getConfig();
        authenticatorRestConfigs.forEach(authenticatorRestConfig -> {
            if (!authenticatorRestConfig.getIsStatic()) {
                String redisValue = redisService.getValue("Authenticator-" + authenticatorRestConfig.getAuthenticatorKey());
                if (StringUtils.isBlank(redisValue)) {
                    stopExchangeOnInvalidAppKey(exchange);
                    return;
                }
            }
            String value;
            if (authenticatorRestConfig.getKeyLocation() == AuthenticatorKeyLocation.Header) {
                value = exchange.getIn().getHeader(authenticatorRestConfig.getAuthenticatorKey(), String.class);
            } else if (authenticatorRestConfig.getKeyLocation() == AuthenticatorKeyLocation.Body){
                Map<String, String> body = exchange.getIn().getBody(Map.class);
                value = body.get(authenticatorRestConfig.getAuthenticatorKey());
            } else {
                if (param !=null ) {
                    value = param.get(authenticatorRestConfig.getAuthenticatorKey());
                } else {
                    value = "";
                }
            }
            if (StringUtils.isBlank(value) || !value.equals(authenticatorRestConfig.getAuthenticatorValue())) {
                stopExchangeOnInvalidAppKey(exchange);
            }
        });
        exchange.getOut().setBody(param);
    }
}
