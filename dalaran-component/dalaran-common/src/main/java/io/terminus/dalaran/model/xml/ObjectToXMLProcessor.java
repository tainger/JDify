package io.terminus.dalaran.model.xml;

import io.terminus.dalaran.model.schema.XMLSchema;
import io.terminus.dalaran.model.utils.ModelUtils;
import io.terminus.dalaran.model.utils.XMLUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ObjectToXMLProcessor implements Processor {

    private XMLSchema schema;

    public ObjectToXMLProcessor(XMLSchema schema) {
        this.schema = schema;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object in = exchange.getIn().getBody();
        String body = ModelUtils.parseBody(in);
        String out = XMLUtils.toXML(body);
        exchange.getOut().setBody(out);
    }
}
