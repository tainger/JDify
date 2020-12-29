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
        String reqSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws2=\"http://ws2ws.csbTest.csb/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ws2:ws2ws>\n" +
                "         <pageSize>10</pageSize>\n" +
                "      </ws2:ws2ws>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        WSParams params;
        if (StringUtils.isNotBlank(config.getAccessKey()) && StringUtils.isNotBlank(config.getSecretKey())) {
            params = WSParams.create().api(config.getApi()).version(config.getVersion())
                    .accessKey(config.getAccessKey()).secretKey(config.getSecretKey());
        } else {
            params = WSParams.create().api(config.getApi()).version(config.getVersion());
        }
        Dispatch<SOAPMessage> dispatch = WSInvoker.createDispatch(params, config.getNameSpace(), config.getServiceName(),
                config.getPortName(), config.getSoapActionUri(), false, config.getEndpoint());
        SOAPMessage request = WSInvoker.createSOAPMessage(false, reqSoap);
        SOAPMessage response = dispatch.invoke(request);
        exchange.getOut().setBody(response);
    }
}
