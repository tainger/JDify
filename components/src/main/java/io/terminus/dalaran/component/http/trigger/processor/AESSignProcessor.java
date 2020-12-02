package io.terminus.dalaran.component.http.trigger.processor;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.component.trigger.rest.RestConfig;
import io.terminus.dalaran.component.utils.AESUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

public class AESSignProcessor implements Processor {

    private RestConfig config;

    public AESSignProcessor(RestConfig config) {
        this.config = config;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        String originData = (String)body.get("data");
        Object out = JSON.parse(AESUtils.decrypt(originData, config.getSecret()));
        exchange.getOut().setBody(out);
    }
}
