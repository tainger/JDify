package io.terminus.dalaran.component.processor.as2;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.component.processor.as2.model.EDIRequestResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.as2.api.entity.AS2MessageDispositionNotificationEntity;
import org.apache.camel.component.as2.api.entity.DispositionNotificationMultipartReportEntity;
import org.apache.camel.component.as2.api.entity.MultipartSignedEntity;
import org.apache.camel.component.as2.api.entity.TextPlainEntity;
import org.apache.commons.io.IOUtils;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpCoreContext;

import java.util.Arrays;

@Slf4j
public class AS2ClientDataProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object in = exchange.getIn().getBody();
        EDIRequestResult result = new EDIRequestResult();
        log.info("body class: " + in.getClass().getName());
        if (in instanceof HttpCoreContext) {
            log.info("body is instance of HttpCoreContext!" );
            HttpCoreContext httpContext = exchange.getIn().getBody(HttpCoreContext.class);
            BasicHttpResponse response =  (BasicHttpResponse) httpContext.getResponse();
            log.info("response headers: " + Arrays.toString(response.getAllHeaders()));
            try {
                String responseMessageId = response.getFirstHeader("Message-Id").getValue();
                result.setResponseMessageId(responseMessageId);
                MultipartSignedEntity multipartSignedEntity = (MultipartSignedEntity)response.getEntity();
                DispositionNotificationMultipartReportEntity reportEntity = (DispositionNotificationMultipartReportEntity) multipartSignedEntity.getPart(0);
                TextPlainEntity plainEntity = (TextPlainEntity) reportEntity.getPart(0);
                result.setMessage(plainEntity.getText());
                AS2MessageDispositionNotificationEntity notificationEntity = (AS2MessageDispositionNotificationEntity)reportEntity.getPart(1);
                String status = notificationEntity.getDispositionType().getType();
                result.setStatus(status);
                result.setRequestMessageId(notificationEntity.getOriginalMessageId());
                if (!status.equalsIgnoreCase("processed")) {
                    result.setSuccess(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.warn(IOUtils.toString(response.getEntity().getContent()));
            }
        }
        exchange.getOut().setBody(JSON.toJSONString(result));
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
