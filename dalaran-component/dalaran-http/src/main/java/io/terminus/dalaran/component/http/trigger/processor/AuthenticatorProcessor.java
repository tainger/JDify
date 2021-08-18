package io.terminus.dalaran.component.http.trigger.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.terminus.dalaran.component.authenticator.AuthenticatorBasic;
import io.terminus.dalaran.component.authenticator.AuthenticatorConfigType;
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

    private AuthenticatorConfigType authenticator;

    private RedisService redisService;

    public AuthenticatorProcessor(AuthenticatorConfigType authenticator, RedisService redisService) {
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
        } else if (authenticator.getType().equals("SignAuthenticator")) {
            checkSign(exchange, body);
        }
    }

    public void checkBasicAuthenticator(Exchange exchange, Map<String, String> body) {
        List<AuthenticatorBasic> authenticatorBasics = JSON.parseArray(JSON.toJSONString(authenticator.getConfig()), AuthenticatorBasic.class);
        authenticatorBasics.forEach(authenticatorBasic -> {
            String value = authenticatorBasic.getAuthenticatorValue();
            String requestValue;
            if (!authenticatorBasic.getIsStatic()) {
                value = redisService.getValue("Authenticator-" + authenticatorBasic.getAuthenticatorKey());
                if (StringUtils.isBlank(value)) {
                    stopExchangeOnInvalidAppKey(exchange);
                    return;
                }
            }
            if (authenticatorBasic.getKeyLocation().name().equals(AuthenticatorKeyLocation.Header.name())) {
                requestValue = exchange.getIn().getHeader(authenticatorBasic.getAuthenticatorKey(), String.class);
            } else if (authenticatorBasic.getKeyLocation().name().equals(AuthenticatorKeyLocation.QueryParam.name())) {
                requestValue = exchange.getIn().getHeader(authenticatorBasic.getAuthenticatorKey(), String.class);
            } else {
                if (body != null) {
                    requestValue = body.get(authenticatorBasic.getAuthenticatorKey());
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
            String appKey;
            String requestSign;
            if (authenticatorSign.getSignLocation().name().equals(AuthenticatorKeyLocation.Header.name())) {
                appKey = exchange.getIn().getHeader(AUTH_APP_KEY, String.class);
                requestSign = exchange.getIn().getHeader(AUTH_SIGN, String.class);
            } else if (authenticatorSign.getSignLocation().name().equals(AuthenticatorKeyLocation.QueryParam.name())) {
                appKey = exchange.getIn().getHeader(AUTH_APP_KEY, String.class);
                requestSign = exchange.getIn().getHeader(AUTH_SIGN, String.class);
            } else {
                appKey = body.get(AUTH_APP_KEY);
                requestSign = body.get(AUTH_SIGN);
            }
            if (StringUtils.isBlank(appKey)) {
                stopExchangeOnMissingAppKey(exchange);
                return;
            }
            if (!appKey.equals(authenticatorSign.getAppKey())) {
                stopExchangeOnInvalidAppKey(exchange);
                return;
            }
            if (StringUtils.isNotBlank(requestSign)) {
                String appSecret = authenticatorSign.getAppSecret();
                Map<String, Object> sortedBody = new TreeMap<>(body);
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
            String appKey;
            String requestSign;
            if (authenticatorSign.getSignLocation().name().equals(AuthenticatorKeyLocation.Header.name())) {
                appKey = exchange.getIn().getHeader(AUTH_APP_KEY, String.class);
                requestSign = exchange.getIn().getHeader(AUTH_SIGN, String.class);
            } else if (authenticatorSign.getSignLocation().name().equals(AuthenticatorKeyLocation.Body.name())) {
                Map<String, String> body = exchange.getIn().getBody(Map.class);
                appKey = body.get(AUTH_APP_KEY);
                requestSign = body.get(AUTH_SIGN);
            } else {
                appKey = param.get(AUTH_APP_KEY);
                requestSign = param.get(AUTH_SIGN);
            }
            if (StringUtils.isBlank(appKey)) {
                stopExchangeOnMissingAppKey(exchange);
                return;
            }
            if (!appKey.equals(authenticatorSign.getAppKey())) {
                stopExchangeOnInvalidAppKey(exchange);
                return;
            }
            if (StringUtils.isNotBlank(requestSign)) {
                String appSecret = authenticatorSign.getAppSecret();
                Map<String, Object> sortedBody = new TreeMap<>(param);
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
            List<AuthenticatorBasic> authenticatorBasics = JSON.parseArray(JSON.toJSONString(authenticator.getConfig()), AuthenticatorBasic.class);
            authenticatorBasics.forEach(authenticatorBasic -> {
                String value = authenticatorBasic.getAuthenticatorValue();
                String requestValue;
                if (!authenticatorBasic.getIsStatic()) {
                    value = redisService.getValue("Authenticator-" + authenticatorBasic.getAuthenticatorKey());
                    if (StringUtils.isBlank(value)) {
                        stopExchangeOnInvalidAppKey(exchange);
                        return;
                    }
                }
                if (authenticatorBasic.getKeyLocation().name().equals(AuthenticatorKeyLocation.Header.name())) {
                    requestValue = exchange.getIn().getHeader(authenticatorBasic.getAuthenticatorKey(), String.class);
                } else if (authenticatorBasic.getKeyLocation().name().equals(AuthenticatorKeyLocation.Body.name())) {
                    Map<String, String> body = exchange.getIn().getBody(Map.class);
                    requestValue = body.get(authenticatorBasic.getAuthenticatorKey());
                } else {
                    if (param != null) {
                        requestValue = param.get(authenticatorBasic.getAuthenticatorKey());
                    } else {
                        requestValue = "";
                    }
                }
                if (StringUtils.isBlank(requestValue) || !requestValue.equals(value)) {
                    stopExchangeOnInvalidAppKey(exchange);
                }
            });
            exchange.getOut().setBody(param);
        } else if (authenticator.getType().equals("SignAuthenticator")) {
            checkGetSign(exchange, param);
        }
    }
}
