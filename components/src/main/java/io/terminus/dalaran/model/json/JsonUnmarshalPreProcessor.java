package io.terminus.dalaran.model.json;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

@Slf4j
public class JsonUnmarshalPreProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object in = exchange.getIn().getBody();
        if (in instanceof byte[]) {
            Object out = JSON.parse((byte[])in);
            log.info("JsonUnmarshalPreProcessor out: " + out);
            exchange.getOut().setBody(out);
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }
}
