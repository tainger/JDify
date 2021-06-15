package io.terminus.dalaran.component.http.trigger.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.terminus.dalaran.component.authenticator.BasicAuthenticatorConfig;
import io.terminus.dalaran.component.authenticator.DalaranAuthenticator;
import io.terminus.dalaran.component.authenticator.AuthenticatorSign;
import io.terminus.dalaran.component.http.trigger.utils.SignUtils;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.model.authenticator.AuthenticatorKeyLocation;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static io.terminus.dalaran.DalaranConstants.AUTH_APP_KEY;
import static io.terminus.dalaran.DalaranConstants.AUTH_SIGN;
import static io.terminus.dalaran.component.http.trigger.utils.SignUtils.*;

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
        if (authenticator.getType().equals("BasicAuthenticator")) {
            checkBasicAuthenticator(exchange, body);
        } else if (authenticator.getType().equals("Sign")) {
            checkSign(exchange, body);
        }
    }

    public void checkBasicAuthenticator(Exchange exchange, Map<String, String> body) {
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
            } else if (StringUtils.equals(basicAuthenticatorConfig.getKeyLocation().name(), AuthenticatorKeyLocation.QueryParam.name())) {
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

    public void checkSign(Exchange exchange, Map<String, String> body) {
        List<AuthenticatorSign> authenticatorSigns = JSON.parseArray(JSON.toJSONString(authenticator.getConfig()), AuthenticatorSign.class);
        authenticatorSigns.forEach(authenticatorSign -> {
            String appKey = body.get(AUTH_APP_KEY);
            if (StringUtils.isBlank(appKey)) {
                stopExchangeOnMissingAppKey(exchange);
                return;
            }
            if (StringUtils.equals(appKey, authenticatorSign.getAppKey())) {
                stopExchangeOnInvalidAppKey(exchange);
                return;
            }
            String requestSign;
            if (StringUtils.equals(authenticatorSign.getSignLocation().name(), AuthenticatorKeyLocation.Header.name())) {
                requestSign = exchange.getIn().getHeader(AUTH_SIGN, String.class);
            } else if (StringUtils.equals(authenticatorSign.getSignLocation().name(), AuthenticatorKeyLocation.QueryParam.name())) {
                requestSign = exchange.getIn().getHeader(AUTH_SIGN, String.class);
            } else {
                requestSign = body.get(AUTH_SIGN);
            }
            if (StringUtils.isNotBlank(requestSign)) {
                String appSecret = authenticatorSign.getAppSecret();
                Map<String, String> sortedBody = new TreeMap<>(body);
                sortedBody.remove(AUTH_SIGN);
                String bodyString = sortedBody.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("&")) + appSecret;
                if (SignUtils.signEquals(bodyString, requestSign)) {
                    exchange.getOut().setBody(body);
                } else {
                    stopExchangeOnInvalidSign(exchange);
                }
            } else {
                stopExchangeOnMissingSign(exchange);
            }
        });
    }

    public void checkGetSign(Exchange exchange, Map<String, String> param) {
        List<AuthenticatorSign> authenticatorSigns = JSON.parseArray(JSON.toJSONString(authenticator.getConfig()), AuthenticatorSign.class);
        authenticatorSigns.forEach(authenticatorSign -> {
            String appKey = param.get(AUTH_APP_KEY);
            if (StringUtils.isBlank(appKey)) {
                stopExchangeOnMissingAppKey(exchange);
                return;
            }
            if (StringUtils.equals(appKey, authenticatorSign.getAppKey())) {
                stopExchangeOnInvalidAppKey(exchange);
                return;
            }
            String requestSign;
            if (StringUtils.equals(authenticatorSign.getSignLocation().name(), AuthenticatorKeyLocation.Header.name())) {
                requestSign = exchange.getIn().getHeader(AUTH_SIGN, String.class);
            } else if (StringUtils.equals(authenticatorSign.getSignLocation().name(), AuthenticatorKeyLocation.Body.name())) {
                Map<String, String> body = exchange.getIn().getBody(Map.class);
                requestSign = body.get(AUTH_SIGN);
            } else {
                requestSign = param.get(AUTH_SIGN);
            }
            if (StringUtils.isNotBlank(requestSign)) {
                String appSecret = authenticatorSign.getAppSecret();
                Map<String, String> sortedBody = new TreeMap<>(param);
                sortedBody.remove(AUTH_SIGN);
                String bodyString = sortedBody.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("&")) + appSecret;
                if (SignUtils.signEquals(bodyString, requestSign)) {
                    exchange.getOut().setBody(param);
                } else {
                    stopExchangeOnInvalidSign(exchange);
                }
            } else {
                stopExchangeOnMissingSign(exchange);
            }
        });
    }

    public void checkGetValue(Exchange exchange, Map<String, String> param) {
        if (authenticator.getType().equals("BasicAuthenticator")) {
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
                } else if (StringUtils.equals(basicAuthenticatorConfig.getKeyLocation().name(), AuthenticatorKeyLocation.Body.name())) {
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
        } else if (authenticator.getType().equals("Sign")) {
            checkGetSign(exchange, param);
        }
    }
}
