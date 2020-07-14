package io.terminus.dalaran.model.json;

import com.alibaba.fastjson.JSON;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class JsonUnmarshalPreProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object in = exchange.getIn().getBody();
        if (in instanceof byte[]) {
            exchange.getOut().setBody(JSON.parse((byte[])in));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }
}
