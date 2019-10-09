package io.terminus.dalaran.model.soap.processor;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.terminus.dalaran.model.soap.jackson.DalaranObjectDeserializer;
import io.terminus.dalaran.model.soap.jackson.DalaranXMLStreamReader;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;

import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/6
 */
public class SoapToObjectProcessor implements Processor, Traceable {

    @Override
    public void process(Exchange exchange) throws Exception {
        String in = exchange.getIn().getBody(String.class);
        Object body = parseSoapBody(in);
        exchange.getOut().setBody(body);
    }

    private Object parseSoapBody(String body) throws Exception {
        InputStream is = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        DalaranXMLStreamReader sr = new DalaranXMLStreamReader(XMLInputFactory.newFactory().createXMLStreamReader(is));
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new SimpleModule().addDeserializer(Object.class, new DalaranObjectDeserializer()));
        Map map = (Map) xmlMapper.readValue(sr, Object.class);
        return map.get("Body");
    }

    @Override
    public String getTraceLabel() {
        return "SoapConvert: SoapToObject";
    }
}
