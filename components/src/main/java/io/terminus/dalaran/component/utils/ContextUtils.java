package io.terminus.dalaran.component.utils;

import org.apache.camel.Exchange;

import java.util.HashMap;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.CAMEL_CORRELATION_ID;
import static io.terminus.dalaran.DalaranConstants.DALARAN_CONTEXT_EXCHANGE;

public class ContextUtils {

    public static Map<String, Object> setExchange(Exchange exchange) {
        Map<String, Object> parent = exchange.getProperty(DALARAN_CONTEXT_EXCHANGE + exchange.getProperty(CAMEL_CORRELATION_ID, String.class), Map.class);
        Map<String, Object> context = exchange.getProperty(DALARAN_CONTEXT_EXCHANGE + exchange.getExchangeId(), Map.class);
        if (context == null) {
            if (parent != null) {
                context = parent;
            } else {
                context = new HashMap<>();
            }
        } else {
            if (parent != null) {
                for (Map.Entry<String, Object> entry: parent.entrySet()) {
                    if (!context.containsKey(entry.getKey())) {
                        context.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        exchange.setProperty(DALARAN_CONTEXT_EXCHANGE + exchange.getExchangeId(), context);
        return context;
    }
}
