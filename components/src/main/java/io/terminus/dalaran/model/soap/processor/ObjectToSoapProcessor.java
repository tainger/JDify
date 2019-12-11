package io.terminus.dalaran.model.soap.processor;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.SoapSchema;
import io.terminus.dalaran.model.schema.SoapSchemaOperation;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.commons.collections.MapUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/6
 */
public class ObjectToSoapProcessor implements Processor, Traceable {

    private final Map<String, ModelField> modelFields;

    private final SoapSchemaOperation soapOperationConfig;

    private String PREFIX;

    public ObjectToSoapProcessor(SoapSchema schema) {
        this.modelFields = schema.getFields();
        this.soapOperationConfig = schema.getOperationConfig();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        Object rst = buildSoapBody(modelFields.get(DalaranConstants.MODEL_ROOT), body);
        exchange.getOut().setBody(rst);
    }

    public String buildSoapBody(ModelField modelField, Object body) throws Exception {
        PREFIX = soapOperationConfig.getPrefix();
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        soapEnvelope.addNamespaceDeclaration(PREFIX, soapOperationConfig.getTargetNamespace());
        SOAPBody soapBody = soapEnvelope.getBody();
        buildRoot(modelField.getFields(), body, soapBody);
        if (soapOperationConfig.getHeader() != null) {
            SOAPHeader header = soapEnvelope.getHeader();
            buildHeader(soapOperationConfig.getHeader().getModelSchema().getFields().get(DalaranConstants.MODEL_ROOT).getFields(), soapOperationConfig.getHeaderValues(), header);
        }
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
        return data.toString();
    }

    private void buildRoot(Map<String, ModelField> modelField, Object body, SOAPElement soapElement) throws Exception {
        if (MapUtils.isEmpty(modelField) || body == null) {
            return;
        }
        for (Map.Entry<String, ModelField> entry : modelField.entrySet()) {
            if (!((Map) body).containsKey(entry.getKey())) {
                continue;
            }
            Object ob = ((Map) body).get(entry.getKey());
            SOAPElement element = soapElement.addChildElement(entry.getKey(), PREFIX);
            if (soapOperationConfig.getBodyContainsXmlns()) {
                element.addNamespaceDeclaration("", soapOperationConfig.getTargetNamespace());
            }
            Map<String, ModelField> child = entry.getValue().getFields();
            buildBody(child, ob, element, soapOperationConfig.getBodyContainsXmlns(), soapOperationConfig.getBodyContainsPrefix());
        }
    }

    private void buildBody(Map<String, ModelField> modelField, Object body, SOAPElement soapElement, Boolean bodyContainsXmlns, Boolean bodyContainsPrefix) throws Exception {
        if (MapUtils.isEmpty(modelField) || body == null) {
            return;
        }
        for (Map.Entry<String, ModelField> entry : modelField.entrySet()) {
            String name = entry.getKey();
            if (!((Map) body).containsKey(name)) {
                continue;
            }
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
                        SOAPElement element;
                        if (bodyContainsPrefix) {
                            element = soapElement.addChildElement(name, PREFIX);
                        } else {
                            element = soapElement.addChildElement(name);
                        }
                        if (bodyContainsXmlns) {
                            element.addNamespaceDeclaration("", soapOperationConfig.getTargetNamespace());
                        }
                        Map<String, ModelField> child = field.getFields();
                        buildBody(child, data, element, bodyContainsXmlns, bodyContainsPrefix);
                    }
                } else {
                    for (Object data: subBody) {
                        SOAPElement element = soapElement.addChildElement(name, PREFIX);
                        element.addNamespaceDeclaration("", soapOperationConfig.getTargetNamespace());
                        element.addTextNode(data.toString());
                    }
                }
            } else if (type == FieldType.OBJECT) {
                SOAPElement element;
                if (bodyContainsPrefix) {
                    element = soapElement.addChildElement(name, PREFIX);
                } else {
                    element = soapElement.addChildElement(name);
                }
                if (bodyContainsXmlns) {
                    element.addNamespaceDeclaration("", soapOperationConfig.getTargetNamespace());
                }
                Map<String, ModelField> child = field.getFields();
                buildBody(child, ob, element, bodyContainsXmlns, bodyContainsPrefix);
            } else {
                SOAPElement element = soapElement.addChildElement(name);
                element.addTextNode(ob.toString());
            }
        }
    }

    private void buildHeader(Map<String, ModelField> modelField, Map<String, Object> values, SOAPElement soapElement) throws Exception {
        if (MapUtils.isEmpty(modelField)) {
            return;
        }
        for (Map.Entry<String, ModelField> entry : modelField.entrySet()) {
            String name = entry.getKey();
            ModelField field = entry.getValue();
            FieldType type = field.getType();

            if (type == FieldType.OBJECT) {
                SOAPElement element = soapElement.addChildElement(name, PREFIX);
                element.addNamespaceDeclaration("", soapOperationConfig.getTargetNamespace());
                Map<String, ModelField> child = field.getFields();
                buildHeader(child, values, element);
            } else {
                SOAPElement element = soapElement.addChildElement(name, PREFIX);
                element.addTextNode(values.getOrDefault(name, "").toString());
            }
        }
    }

    @Override
    public String getTraceLabel() {
        return "SoapConvert: ObjectToSoap";
    }
}
