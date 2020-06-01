package io.terminus.dalaran.component.processor.http;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.component.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;

import java.util.Map;

@Slf4j
public class QuerySignProcessor implements Processor {

    private String apiKey;

    private String apiSecret;

    public QuerySignProcessor(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String queryString = buildSignQueryString(exchange.getIn().getBody());
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        exchange.getOut().setHeader(Exchange.HTTP_QUERY, queryString);
        exchange.getOut().setBody(exchange.getIn().getBody());
    }

    private String buildSignQueryString(Object obj) throws Exception {
        Map inBody;
        log.info("in body class: " + obj.getClass().getName());
        if (obj instanceof byte[]) {
            inBody = JSON.parseObject(IOUtils.toString((byte[]) obj), Map.class);
        } else if (obj instanceof String) {
            inBody = JSON.parseObject((String)obj, Map.class);
        } else {
            inBody = JSON.parseObject(JSON.toJSONString(obj), Map.class);
        }
        log.info("in body: " + inBody.toString());
        Gson gson = new Gson();
        String sign = SignUtils.calculateMD5Signature(gson.toJsonTree(inBody).getAsJsonObject(), apiSecret);
        inBody.put(ComponentConstants.SIGNATURE, sign);
        inBody.put(ComponentConstants.API_KEY, apiKey);
        Map queryKV = Maps.filterEntries(inBody, (Predicate<Map.Entry>) entry -> entry.getValue() != null && entry.getKey() != null);
        return Joiner.on("&").withKeyValueSeparator("=").join(queryKV);
    }
}
