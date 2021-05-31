package io.terminus.dalaran.component.http.trigger.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class BodyProcessor implements Processor {

    private String params;

    public BodyProcessor(String params) {
        this.params = params;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (exchange.getIn().getBody() instanceof JSONArray) {
            return;
        }
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        Map<String, String> paramMap;
        if (!StringUtils.isBlank(params)) {
            String[] paramArray = StringUtils.split(StringUtils.replaceChars(params, " ", ""), ",");
            String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
            if (!StringUtils.isBlank(queryString)) {
                paramMap = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
                if (body == null) {
                    body = new JSONObject();
                }
                for (String param : paramArray) {
                    body.put(param, paramMap.get(param));
                }
            }
        }
        exchange.getOut().setBody(body);
    }
}
