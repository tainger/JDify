package io.terminus.dalaran.model.converter.soap.processor;

import com.predic8.wsdl.Definitions;
import com.predic8.wstool.creator.RequestCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import com.predic8.xml.util.BasicAuthenticationResolver;
import com.sun.org.apache.xpath.internal.operations.Mod;
import groovy.xml.MarkupBuilder;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.converter.soap.model.SoapOperationConfig;
import io.terminus.dalaran.model.converter.soap.model.SoapSchemaOperation;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.commons.collections.MapUtils;
import org.apache.http.entity.StringEntity;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/6
 */
public class ObjectToSoapProcessor implements Processor, Traceable {

    private final Map<String, ModelField> modelFields;

    private final SoapSchemaOperation soapOperationConfig;

    private static final String XPATH = "xpath:";

    private static final String PREFIX = "dalaran";

    public ObjectToSoapProcessor(Map<String, ModelField> modelFields, SoapSchemaOperation soapOperationConfig) {
        this.modelFields = modelFields;
        this.soapOperationConfig = soapOperationConfig;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        Object rst = buildRequest(modelFields.get(DalaranConstants.MODEL_ROOT), body);
        StringEntity stringEntity = new StringEntity(rst.toString());
        stringEntity.setContentType("text/xml");
        exchange.getOut().setBody(stringEntity);
    }

    private Object buildRequest(ModelField modelField, Object body) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        soapEnvelope.addNamespaceDeclaration(PREFIX, soapOperationConfig.getTargetNamespace());
        SOAPBody soapBody = soapEnvelope.getBody();
        buildBody(modelField.getFields(), body, soapBody);
//        MimeHeaders hd = message.getMimeHeaders();
//        hd.addHeader("SOAPAction", "http://sap.com/xi/WebService/soap1.1");
        message.saveChanges();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        message.writeTo(stream);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(stream.toString())));
        OutputFormat format = new OutputFormat(document);
        format.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(data, format);
        serializer.serialize(document);
        return data;
    }

    public Object request(Object body) throws Exception {
        SOAPMessage request = buildRequestMessage(modelFields.get(DalaranConstants.MODEL_ROOT), body);
        SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = connectionFactory.createConnection();
        SOAPMessage response = connection.call(request, soapOperationConfig.getLocation());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        response.writeTo(stream);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source source = response.getSOAPPart().getContent();
        StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result);
        return new String(stream.toByteArray());
    }

    private SOAPMessage buildRequestMessage(ModelField modelField, Object body) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        soapEnvelope.addNamespaceDeclaration(PREFIX, soapOperationConfig.getTargetNamespace());
        SOAPBody soapBody = soapEnvelope.getBody();
        buildBody(modelField.getFields(), body, soapBody);
//        String authorization = new sun.misc.BASE64Encoder().encode(("HYPERS_PI" + ":" + "HYPERS_PI2019").getBytes());
        MimeHeaders hd = message.getMimeHeaders();
//        hd.addHeader("Authorization", "Basic " + authorization);
        hd.addHeader("SOAPAction", "http://sap.com/xi/WebService/soap1.1");
        message.saveChanges();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        message.writeTo(stream);
        return message;
    }

    private void buildBody(Map<String, ModelField> modelField, Object body, SOAPElement soapElement) throws Exception {
        if (MapUtils.isEmpty(modelField)) {
            return;
        }
        for (Map.Entry<String, ModelField> entry : modelField.entrySet()) {
            String name = entry.getKey();
            ModelField field = entry.getValue();
            FieldType type = field.getType();
            Object ob = ((Map) body).get(name);

            if (ob == null) {
                continue;
            }
            if (type == FieldType.ARRAY) {
                List subBody = (List) ob;
                FieldType subType = field.getSubType();
                if (subType == FieldType.OBJECT) {
                    for (Object data: subBody) {
                        SOAPElement element = soapElement.addChildElement(name, PREFIX);
                        Map<String, ModelField> child = field.getFields();
                        buildBody(child, data, element);
                    }
                } else {
                    for (Object data: subBody) {
                        SOAPElement element = soapElement.addChildElement(name, PREFIX);
                        element.addTextNode(data.toString());
                    }
                }
            } else if (type == FieldType.OBJECT) {
                SOAPElement element = soapElement.addChildElement(name, PREFIX);
                Map<String, ModelField> child = field.getFields();
                buildBody(child, ob, element);
            } else {
                SOAPElement element = soapElement.addChildElement(name);
                element.addTextNode(ob.toString());
            }
        }
    }

    @Override
    public String getTraceLabel() {
        return "SoapConvert: ObjectToSoap";
    }
}
