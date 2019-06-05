package io.terminus.dalaran.component.trigger.http;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class QueryStringProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        // 保持输入输出不变
//        exchange.getOut().copyFrom(exchange.getIn());
        String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        if (StringUtils.isNotEmpty(queryString)) {
            Map body = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
            exchange.getOut().setBody(JSON.toJSONString(body));
        } else {
            exchange.getOut().setBody(exchange.getIn().getBody());
        }
    }
}
