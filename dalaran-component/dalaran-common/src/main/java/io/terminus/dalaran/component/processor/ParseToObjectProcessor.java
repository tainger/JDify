package io.terminus.dalaran.component.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;

import java.util.Arrays;

@Slf4j
public class ParseToObjectProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object in = exchange.getIn().getBody();
        JSON out = new JSONObject();
        if (in == null) {
            out = new JSONObject();
        }
        try {
            if (in instanceof byte[]) {
                out = (JSON)JSON.parse(IOUtils.toString((byte[]) in));
            } else if (in instanceof String) {
                out = (JSON)JSON.parse((String)in);
            } else if (in instanceof char[]) {
                out = (JSON)JSON.parse(Arrays.toString(((char[]) in)));
            } else {
                out = (JSON)JSON.parse(JSON.toJSONString(in));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("parse2object: " + out.toJSONString());
        log.info("parse2object: " + out.getClass().getName());
        exchange.getOut().setBody(out);
    }
}
