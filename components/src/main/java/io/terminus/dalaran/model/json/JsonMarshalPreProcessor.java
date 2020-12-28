package io.terminus.dalaran.model.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

@Slf4j
public class JsonMarshalPreProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object in = exchange.getIn().getBody();
        if (in instanceof byte[]) {
            exchange.getOut().setBody(JSON.toJSONString(JSON.parse((byte[])in), SerializerFeature.WriteMapNullValue));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        } else {
            exchange.getOut().setBody(JSON.toJSONString(in, SerializerFeature.WriteMapNullValue));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }
}
