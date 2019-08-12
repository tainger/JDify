package io.terminus.dalaran.component.trigger.rest;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static io.terminus.dalaran.DalaranConstants.AUTH_APP_KEY;
import static io.terminus.dalaran.DalaranConstants.AUTH_SIGN;
import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;

public class QueryStringSignProcessor implements Processor {

    private Map<String, String> clientMapper;

    private HashFunction md5 = Hashing.md5();

    QueryStringSignProcessor(Map<String, String> clientMapper) {
        this.clientMapper = clientMapper;
    }

    // TODO 要加点 log, 否则回头查的时候有点蛋疼...
    @Override
    public void process(Exchange exchange) {
        String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        if (StringUtils.isNotEmpty(queryString)) {
            Map<String, String> body = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
            Map<String, String> sortedBody = new TreeMap<>(body);
            sortedBody.remove(AUTH_SIGN);
            String appKey = body.get(AUTH_APP_KEY);
            if (StringUtils.isNotEmpty(appKey) && StringUtils.isNotEmpty(clientMapper.get(appKey))) {
                String sortedQueryString = sortedBody.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("&"));

                String sign = md5.hashString(sortedQueryString + clientMapper.get(appKey), Charsets.UTF_8).toString();
                if (StringUtils.equalsIgnoreCase(sign, body.get(AUTH_SIGN))) {
                    exchange.getOut().setBody(body);
                    return;
                }
            }
        }
        // return http status code 401
        endProcessor(exchange);
    }

    private void endProcessor(Exchange exchange) {
        exchange.getOut().setHeader(HTTP_RESPONSE_CODE, 401);
        exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
    }
}
