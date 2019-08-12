package io.terminus.dalaran.component.trigger.rest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;

public class BodySignProcessor implements Processor {
    private Map<String, String> clientMapper;

    BodySignProcessor(Map<String, String> clientMapper) {
        this.clientMapper = clientMapper;
    }

    @Override
    public void process(Exchange exchange) {
        SignBodyModel bodyModel = exchange.getIn().getBody(SignBodyModel.class);
        String secret = clientMapper.get(bodyModel.getAppKey());
        if (bodyModel.checkSign(secret)) {
            exchange.getOut().setBody(bodyModel.getData());
            return;
        }
        // return http status code 401
        exchange.getOut().setHeader(HTTP_RESPONSE_CODE, 401);
        exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
    }
}
