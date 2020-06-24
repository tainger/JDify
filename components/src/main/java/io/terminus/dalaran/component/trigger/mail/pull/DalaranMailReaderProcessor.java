package io.terminus.dalaran.component.trigger.mail.pull;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.activation.DataHandler;
import java.util.HashMap;
import java.util.Map;

public class DalaranMailReaderProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, DataHandler> attachments = exchange.getIn().getAttachments();
        Map<String, String> fileData = new HashMap<>();
        if (attachments.size() > 0) {
            for (String name : attachments.keySet()) {
                DataHandler dh = attachments.get(name);
                String filename = dh.getName();
                String data = exchange.getContext().getTypeConverter()
                        .convertTo(String.class, dh.getInputStream());
                fileData.put(filename, data);
            }
        }
        exchange.getOut().setBody(fileData);
    }
}
