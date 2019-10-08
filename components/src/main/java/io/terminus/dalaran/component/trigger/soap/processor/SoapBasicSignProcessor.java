package io.terminus.dalaran.component.trigger.soap.processor;

import io.terminus.dalaran.component.trigger.rest.processor.SignProcessor;
import io.terminus.dalaran.component.trigger.rest.utils.SignUtils;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;

import java.util.HashMap;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.AUTH_APP_KEY;
import static io.terminus.dalaran.DalaranConstants.AUTH_SIGN;
import static io.terminus.dalaran.component.trigger.rest.utils.SignUtils.*;

public class SoapBasicSignProcessor extends SignProcessor {

    private static final String AUTHENTICATION = "Authorization";

    public SoapBasicSignProcessor(Map<String, String> clientMapper) {
        super(clientMapper);
    }

    @Override
    public void process(Exchange exchange) {
        String authentication = exchange.getIn().getHeader(AUTHENTICATION, String.class);
        if (StringUtils.isBlank(authentication)) {
            SignUtils.stopExchangeOnInvalidBasicAuth(exchange);
            return;
        }
        try {
            String[] account = new String(new BASE64Decoder().decodeBuffer(StringUtils.substringAfter(authentication,  " "))).split(":");
            Map<String, String> body = new HashMap<>();
            body.put(AUTH_APP_KEY, account[0]);
            body.put(AUTH_SIGN, account[1]);
            Object in = exchange.getIn().getBody();
            checkSign(exchange, body, in);
        } catch (Exception e) {
            e.printStackTrace();
            SignUtils.stopExchangeOnDecodeError(exchange);
        }
    }

    private void checkSign(Exchange exchange, Map<String, String> body, Object data) {
        String appKey = body.get(AUTH_APP_KEY);
        if (StringUtils.isEmpty(appKey)) {
            stopExchangeOnMissingAppKey(exchange);
            return;
        }
        String sign = body.get(AUTH_SIGN);
        if (StringUtils.isEmpty(sign)) {
            stopExchangeOnMissingSign(exchange);
            return;
        }
        String secret = super.getClientMapper().get(appKey);
        if (StringUtils.isEmpty(secret)) {
            stopExchangeOnInvalidAppKey(exchange);
            return;
        }
        if (!StringUtils.equals(sign, secret)) {
            stopExchangeOnInvalidSign(exchange);
            return;
        }
        exchange.getOut().setBody(data);
    }
}
