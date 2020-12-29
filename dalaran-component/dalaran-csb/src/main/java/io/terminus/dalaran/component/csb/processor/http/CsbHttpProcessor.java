package io.terminus.dalaran.component.csb.processor.http;

import com.alibaba.csb.sdk.*;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CsbHttpProcessor implements Processor {

    private CsbHttpClientConfig config;

    private HttpParameters.Builder builder;

    public CsbHttpProcessor(CsbHttpClientConfig config, HttpParameters.Builder builder) {
        this.config = config;
        this.builder = builder;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if(StringUtils.isNotBlank(config.getHeaders())) {
            Map<String, String> headerMap = buildValues(exchange, config.getHeaders());
            for (String key: headerMap.keySet()) {
                builder.putHeaderParamsMap(key, headerMap.get(key));
            }
        }
        String result = null;
        if(config.getMethod() == CsbHttpMethod.GET) {
            builder.contentType("application/x-www-form-urlencoded;charset=utf-8");
            Map<String, Object> params = buildQueryString(exchange.getIn().getBody());
            for (String key : params.keySet()) {
                builder.putParamsMap(key, params.get(key).toString());
            }
            try {
                HttpReturn res = HttpCaller.invokeReturn(builder.build());
                result = res.getResponseStr();
            } catch (HttpCallerException e) {
                e.printStackTrace();
            }
        } else {
            builder.contentType("application/octet-stream");
            ContentBody cb = new ContentBody(exchange.getIn().getBody().toString());
            builder.contentBody(cb);
            try {
                HttpReturn res = HttpCaller.invokeReturn(builder.build());
                result = res.getResponseStr();
            } catch (HttpCallerException e) {
                e.printStackTrace();
            }
        }
        log.info("build: " + builder.build().toString());
        log.info("result: " + result);

        exchange.getOut().setBody(result);
    }

    private Map<String, Object> buildQueryString(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map inBody;
        if (obj instanceof byte[]) {
            inBody = JSON.parseObject(IOUtils.toString((byte[]) obj,"UTF-8"), Map.class);
        } else if (obj instanceof String) {
            inBody = JSON.parseObject((String)obj, Map.class);
        } else {
            inBody = JSON.parseObject(JSON.toJSONString(obj), Map.class);
        }
        return Maps.filterEntries(inBody, (Predicate<Map.Entry>) entry -> entry.getValue() != null && entry.getKey() != null);
    }

    private Map<String, String> buildValues(Exchange exchange, String params) {
        String contextKey = "DalaranContextExchange" + exchange.getExchangeId();
        Map<String, Object> contextValues = (Map)exchange.getProperties().get(contextKey);
        Map<String, String> values = new HashMap<>();
        String[] headerNames = StringUtils.split(StringUtils.replaceChars(params, " ", ""), ",");
        for (String name: headerNames) {
            values.put(name, (String)contextValues.get(name));
        }
        return values;
    }

}
