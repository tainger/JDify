package io.terminus.dalaran.component.trigger.rest.utils;

import com.google.common.base.Charsets;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.hash.Hashing;
import org.apache.http.entity.ContentType;

import static org.apache.camel.Exchange.CONTENT_TYPE;
import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;

public class SignUtils {

    public static boolean signEquals(String data, String sign) {
        String backendSign = Hashing.md5().hashString(data, Charsets.UTF_8).toString();
        return StringUtils.equalsIgnoreCase(sign, backendSign);
    }

    public static void stopExchangeOnMissingAppKey(Exchange exchange) {
        stopExchange(exchange, 1001, "Missing App Key");
    }

    public static void stopExchangeOnInvalidAppKey(Exchange exchange) {
        stopExchange(exchange, 1002, "Invalid App Key");
    }

    public static void stopExchangeOnMissingTimestamp(Exchange exchange) {
        stopExchange(exchange, 1005, "Missing Timestamp");
    }

    public static void stopExchangeOnInvalidTimestamp(Exchange exchange) {
        stopExchange(exchange, 1006, "Invalid Timestamp");
    }

    public static void stopExchangeOnMissingSign(Exchange exchange) {
        stopExchange(exchange, 1003, "Missing Signature");
    }

    public static void stopExchangeOnInvalidSign(Exchange exchange) {
        stopExchange(exchange, 1004, "Invalid Signature");
    }

    public static void stopExchange(Exchange exchange, int errorCode, String message) {
        // return http status code 403
        exchange.getOut().setHeader(HTTP_RESPONSE_CODE, 403);
        exchange.getOut().setHeader(CONTENT_TYPE, ContentType.TEXT_PLAIN);
        exchange.getOut().setBody(message);
        exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
    }

}
