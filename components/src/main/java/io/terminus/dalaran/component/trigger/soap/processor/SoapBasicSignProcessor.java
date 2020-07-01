package io.terminus.dalaran.component.trigger.soap.processor;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.trigger.soap.utils.SoapSignUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SoapBasicSignProcessor implements Processor {

    private static final String AUTHENTICATION = "Authorization";

    private Map<String, String> clientMapper;

    public SoapBasicSignProcessor(Map<String, String> clientMapper) {
        this.clientMapper = clientMapper;
    }

    @Override
    public void process(Exchange exchange) {
        String authentication = exchange.getIn().getHeader(AUTHENTICATION, String.class);
        if (StringUtils.isBlank(authentication)) {
            SoapSignUtils.stopExchangeOnInvalidBasicAuth(exchange);
            return;
        }
        try {
            String[] account = new String(Base64.getDecoder().decode(StringUtils.substringAfter(authentication,  " "))).split(":");
            Map<String, String> body = new HashMap<>();
            body.put(DalaranConstants.AUTH_APP_KEY, account[0]);
            body.put(DalaranConstants.AUTH_SIGN, account[1]);
            Object in = exchange.getIn().getBody();
            checkSign(exchange, body, in);
        } catch (Exception e) {
            e.printStackTrace();
            SoapSignUtils.stopExchangeOnDecodeError(exchange);
        }
    }

    private void checkSign(Exchange exchange, Map<String, String> body, Object data) {
        String appKey = body.get(DalaranConstants.AUTH_APP_KEY);
        if (StringUtils.isEmpty(appKey)) {
            SoapSignUtils.stopExchangeOnMissingAppKey(exchange);
            return;
        }
        String sign = body.get(DalaranConstants.AUTH_SIGN);
        if (StringUtils.isEmpty(sign)) {
            SoapSignUtils.stopExchangeOnMissingSign(exchange);
            return;
        }
        String secret = clientMapper.get(appKey);
        if (StringUtils.isEmpty(secret)) {
            SoapSignUtils.stopExchangeOnInvalidAppKey(exchange);
            return;
        }
        if (!StringUtils.equals(sign, secret)) {
            SoapSignUtils.stopExchangeOnInvalidSign(exchange);
            return;
        }
        exchange.getOut().setBody(data);
    }
}
