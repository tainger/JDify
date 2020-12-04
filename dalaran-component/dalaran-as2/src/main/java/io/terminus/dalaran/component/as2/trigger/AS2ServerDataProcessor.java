package io.terminus.dalaran.component.as2.trigger;

import com.google.common.collect.Maps;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.as2.api.entity.ApplicationEDIEntity;
import org.apache.camel.component.as2.api.entity.ApplicationPkcs7MimeEnvelopedDataEntity;
import org.apache.camel.component.as2.api.entity.MimeEntity;
import org.apache.camel.component.as2.api.entity.MultipartSignedEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.util.Map;

public class AS2ServerDataProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(AS2ServerDataProcessor.class);

    private PrivateKey privateKey;

    public AS2ServerDataProcessor(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info(exchange.getIn().getBody().getClass().getName());
        BasicHttpContext httpContext = exchange.getIn().getBody(BasicHttpContext.class);
        logger.info("context: " + httpContext.toString());
        logger.info("request class: " + httpContext.getAttribute("http.request").getClass().getName());

        Object httpRequest = httpContext.getAttribute("http.request");
        if (httpRequest instanceof BasicHttpEntityEnclosingRequest) {

            BasicHttpEntityEnclosingRequest request = (BasicHttpEntityEnclosingRequest) httpContext.getAttribute("http.request");
            logger.info("request entity class: " + request.getEntity().getClass().getName());
            ApplicationPkcs7MimeEnvelopedDataEntity applicationPkcs7MimeEnvelopedDataEntity = (ApplicationPkcs7MimeEnvelopedDataEntity)request.getEntity();
            MultipartSignedEntity multipartSignedEntity =  (MultipartSignedEntity)applicationPkcs7MimeEnvelopedDataEntity.getEncryptedEntity(privateKey);

            String ediMessage = null;
            for (int i = 0; i < multipartSignedEntity.getPartCount(); i++) {
                MimeEntity entity = multipartSignedEntity.getPart(i);
                if (entity instanceof ApplicationEDIEntity) {
                    ediMessage = ((ApplicationEDIEntity)entity).getEdiMessage();
                    break;
                }
            }
            logger.info("receive edi message: " + ediMessage);
            Map<String, String> body = Maps.newHashMap();
            body.put("data", ediMessage);
            exchange.getOut().setBody(body);
        }
    }
}
