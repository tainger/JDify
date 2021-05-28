package io.terminus.dalaran.component.http.trigger.processor;

import com.google.common.base.Splitter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class BodyProcessor implements Processor {

    private String params;

    public BodyProcessor(String params) {
        this.params = params;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, String> body = exchange.getIn().getBody(Map.class);
        Map<String, String> paramMap;
        String[] paramArray = params.split(",");
        String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        if (!StringUtils.isEmpty(queryString)) {
            paramMap = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
            for (String param : paramArray) {
                body.put(param, paramMap.get(param));
            }
        }
        exchange.getOut().setBody(body);
    }
}
