package io.terminus.dalaran.component.http.trigger.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class MixMethodProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String queryString = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        if (StringUtils.isNotEmpty(queryString)) {
            Map body = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
            exchange.getOut().setBody(JSON.toJSON(body));
        }
    }
}
