package io.terminus.dalaran.component.http.trigger.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.util.Map;

public class XWwwFormUrlencodedProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String in = (String) exchange.getIn().getBody();
        if (StringUtils.isNotEmpty(in)) {
            //todo decode utf-8, i will do soon
            Map body = Splitter.on("&").withKeyValueSeparator("=").split(in);
            exchange.getOut().setBody(JSON.toJSONString(body));
        }
    }
}
