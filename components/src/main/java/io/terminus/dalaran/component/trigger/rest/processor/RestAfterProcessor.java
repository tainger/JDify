package io.terminus.dalaran.component.trigger.rest.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.component.trigger.rest.model.SignAlgorithm;
import io.terminus.dalaran.component.trigger.rest.model.SignAuthenticatorInfo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.HashMap;
import java.util.Map;

public class RestAfterProcessor implements Processor {

    private SignAuthenticatorInfo authenticatorInfo;

    public RestAfterProcessor(SignAuthenticatorInfo authenticatorInfo) {
        this.authenticatorInfo = authenticatorInfo;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        SignProcessor signProcessor = new SignProcessor(null, authenticatorInfo);
        Object in = exchange.getIn().getBody();
        Map<String, Object> body = new HashMap<>();
        if (in instanceof byte[]) {
            body = new ObjectMapper().readValue((byte[])in, Map.class);
        }
        if (in instanceof String) {
            body = new ObjectMapper().readValue((String) in, Map.class);
        }
        if (in instanceof JSONObject) {
            body = JSON.toJavaObject((JSONObject)in, Map.class);
        }
        String inBody = signProcessor.buildSignBody(body);
        String sign = signProcessor.sign(inBody, authenticatorInfo);
        body.put(ComponentConstants.SIGNATURE, sign);
        body.put(ComponentConstants.SIGNATURE_METHOD, SignAlgorithm.SHA256withRSA);
        exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getOut().setBody(JSON.toJSON(body));
    }
}
