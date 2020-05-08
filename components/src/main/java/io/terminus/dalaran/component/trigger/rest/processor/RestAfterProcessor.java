package io.terminus.dalaran.component.trigger.rest.processor;

import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.component.trigger.rest.model.SignAlgorithm;
import io.terminus.dalaran.component.trigger.rest.model.SignAuthenticatorInfo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

public class RestAfterProcessor implements Processor {

    private SignAuthenticatorInfo authenticatorInfo;

    public RestAfterProcessor(SignAuthenticatorInfo authenticatorInfo) {
        this.authenticatorInfo = authenticatorInfo;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        SignProcessor signProcessor = new SignProcessor(null, authenticatorInfo);
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        String in = signProcessor.buildSignBody(body);
        String sign = signProcessor.sign(in, authenticatorInfo);
        body.put(ComponentConstants.SIGNATURE, sign);
        body.put(ComponentConstants.SIGNATURE_METHOD, SignAlgorithm.SHA256withRSA);
        exchange.getOut().setBody(body);
    }
}
