package io.terminus.dalaran.model.xml;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.model.schema.XMLSchema;
import io.terminus.dalaran.model.utils.ModelUtils;
import io.terminus.dalaran.model.utils.XMLUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class XMLToObjectProcessor implements Processor {

    private XMLSchema schema;

    public XMLToObjectProcessor(XMLSchema schema) {
        this.schema = schema;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object in = exchange.getIn().getBody();
        String body = ModelUtils.parseBody(in);
        Object out = XMLUtils.fromXML(body, schema.getFields().get(DalaranConstants.MODEL_ROOT).getFields());
        exchange.getOut().setBody(out);
    }
}
