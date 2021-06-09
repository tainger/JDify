package io.terminus.dalaran.component.http.trigger.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.terminus.dalaran.component.authenticator.BasicAuthenticatorConfig;
import io.terminus.dalaran.component.authenticator.DalaranAuthenticator;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static io.terminus.dalaran.component.http.trigger.utils.SignUtils.stopExchangeOnInvalidAppKey;

public class AuthenticatorProcessor implements Processor {

    private DalaranAuthenticator authenticator;

    private RedisService redisService;

    public AuthenticatorProcessor(DalaranAuthenticator authenticator, RedisService redisService) {
        this.authenticator = authenticator;
        this.redisService = redisService;
    }

    @Override
    public void process(Exchange exchange) {
        if (exchange.getIn().getBody() instanceof JSONArray) {
            return;
        }
        Map<String, String> body = exchange.getIn().getBody(Map.class);
        checkValue(exchange, body);
    }

    void checkValue(Exchange exchange, Map<String, String> body) {
        List<BasicAuthenticatorConfig> basicAuthenticatorConfigs = JSON.parseArray(JSON.toJSONString(authenticator.getConfig()), BasicAuthenticatorConfig.class);
        basicAuthenticatorConfigs.forEach(basicAuthenticatorConfig -> {
            String value = basicAuthenticatorConfig.getAuthenticatorValue();
            String requestValue;
            if (!basicAuthenticatorConfig.getIsStatic()) {
                value = redisService.getValue("Authenticator-" + basicAuthenticatorConfig.getAuthenticatorKey());
                if (StringUtils.isBlank(value)) {
                    stopExchangeOnInvalidAppKey(exchange);
                    return;
                }
            }
            if (StringUtils.equals(basicAuthenticatorConfig.getKeyLocation().name(), AuthenticatorKeyLocation.Header.name())) {
                requestValue = exchange.getIn().getHeader(basicAuthenticatorConfig.getAuthenticatorKey(), String.class);
            } else if (basicAuthenticatorConfig.getKeyLocation() == AuthenticatorKeyLocation.QueryParam) {
                requestValue = exchange.getIn().getHeader(basicAuthenticatorConfig.getAuthenticatorKey(), String.class);
            } else {
                if (body != null) {
                    requestValue = body.get(basicAuthenticatorConfig.getAuthenticatorKey());
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
        List<BasicAuthenticatorConfig> basicAuthenticatorConfigs = JSON.parseArray(JSON.toJSONString(authenticator.getConfig()), BasicAuthenticatorConfig.class);
        basicAuthenticatorConfigs.forEach(basicAuthenticatorConfig -> {
            String value = basicAuthenticatorConfig.getAuthenticatorValue();
            String requestValue;
            if (!basicAuthenticatorConfig.getIsStatic()) {
                value = redisService.getValue("Authenticator-" + basicAuthenticatorConfig.getAuthenticatorKey());
                if (StringUtils.isBlank(value)) {
                    stopExchangeOnInvalidAppKey(exchange);
                    return;
                }
            }
            if (basicAuthenticatorConfig.getKeyLocation() == AuthenticatorKeyLocation.Header) {
                requestValue = exchange.getIn().getHeader(basicAuthenticatorConfig.getAuthenticatorKey(), String.class);
            } else if (basicAuthenticatorConfig.getKeyLocation() == AuthenticatorKeyLocation.Body) {
                Map<String, String> body = exchange.getIn().getBody(Map.class);
                requestValue = body.get(basicAuthenticatorConfig.getAuthenticatorKey());
            } else {
                if (param != null) {
                    requestValue = param.get(basicAuthenticatorConfig.getAuthenticatorKey());
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
