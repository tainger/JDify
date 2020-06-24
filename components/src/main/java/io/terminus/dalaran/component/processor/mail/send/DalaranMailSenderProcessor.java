package io.terminus.dalaran.component.processor.mail.send;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import javax.activation.DataHandler;

public class DalaranMailSenderProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        byte[] file = in.getBody(byte[].class);
        String fileId = in.getHeader("CamelFileName",String.class);
        in.addAttachment(fileId, new DataHandler(file,"plain/text"));
    }
}
