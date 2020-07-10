package io.terminus.dalaran.component.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.terminus.dalaran.function.xml.DuplicateToArrayJsonNodeDeserializer;
import io.terminus.dalaran.model.soap.jackson.DalaranObjectDeserializer;
import io.terminus.dalaran.model.soap.jackson.DalaranXMLStreamReader;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SoapConvertTest {

    @Test
    public void toObject() throws Exception {
        String body =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope\n" +
                "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:dalaran=\"http://schemas.xmlsoap.org/wsdl\">\n" +
                "    <SOAP-ENV:Header/>\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <dalaran:response>\n" +
                "            <result>0013</result>\n" +
                "        </dalaran:response>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";
        InputStream is = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        DalaranXMLStreamReader sr = new DalaranXMLStreamReader(XMLInputFactory.newFactory().createXMLStreamReader(is));
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new SimpleModule().addDeserializer(Object.class, new DalaranObjectDeserializer()).addDeserializer(JsonNode.class,
                new DuplicateToArrayJsonNodeDeserializer()));
        Map map = (Map) xmlMapper.readValue(sr, Object.class);
        Assert.assertNotNull(map);
    }
}
