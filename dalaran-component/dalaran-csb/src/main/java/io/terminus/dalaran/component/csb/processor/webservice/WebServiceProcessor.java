package io.terminus.dalaran.component.csb.processor.webservice;

import com.alibaba.csb.ws.sdk.WSInvoker;
import com.alibaba.csb.ws.sdk.WSParams;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;

public class WebServiceProcessor implements Processor {

    private WebServiceClientConfig config;

    public WebServiceProcessor(WebServiceClientConfig config) {
        this.config = config;
    }


    @Override
    public void process(Exchange exchange) {
        WSParams params;
        if (StringUtils.isNotBlank(config.getAccessKey()) && StringUtils.isNotBlank(config.getSecretKey())) {
            params = WSParams.create().api(config.getApi()).version(config.getVersion())
                    .accessKey(config.getAccessKey()).secretKey(config.getSecretKey());
        } else {
            params = WSParams.create().api(config.getApi()).version(config.getVersion());
        }
        Dispatch<SOAPMessage> dispatch = WSInvoker.createDispatch(params, config.getNameSpace(), config.getServiceName(),
                config.getPortName(), config.getSoapActionUri(), false, config.getEndpoint());
        SOAPMessage request = WSInvoker.createSOAPMessage(false, config.getReqSoap());
        SOAPMessage response = dispatch.invoke(request);
        exchange.getOut().setBody(response);
    }
}
