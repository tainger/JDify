package io.terminus.dalaran.service.soap;

import com.alibaba.fastjson.JSON;
import com.predic8.schema.*;
import com.predic8.wsdl.*;
import io.terminus.common.utils.JsonMapper;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.annotation.ServiceConnector;
import io.terminus.dalaran.core.model.*;
import io.terminus.dalaran.core.model.converter.soap.model.SoapOperationConfig;
import io.terminus.dalaran.core.model.converter.soap.model.SoapSchemaOperation;
import io.terminus.dalaran.core.model.schema.SoapSchema;
import org.apache.camel.Exchange;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jingdi on 2019/5/27
 */
@ServiceConnector(value = "soap-connector", importConfigType = WSDLImportConfig.class, serviceConfigType = SoapServiceConfig.class)
public class SoapService implements DalaranService<WSDLImportConfig, SoapServiceConfig, SoapOperationConfig> {

    private static final String OPERATION_SPLIT = ":::";

    private static final String HTTP_URI = "%s4://%s";

    @Override
    public void configure(ProcessorDefinition route, SoapOperationConfig soapOperationConfig) {
        String uri = String.format(HTTP_URI, "http", soapOperationConfig.getBaseUrl());
        route.setHeader(Exchange.HTTP_METHOD, Builder.constant(HttpMethod.POST));
        route.setHeader(Exchange.CONTENT_TYPE, Builder.constant("text/xml"));
        route.to(uri);
        // TODO Stream to string
        route.convertBodyTo(String.class);
    }

    @Override
    public SoapOperationConfig getOperationConfig(SoapServiceConfig soapServiceConfig, @NotNull String operationKey) {
        List<SoapOperationConfig> configs = soapServiceConfig.getSoapOperations();
        String[] keys = operationKey.split(OPERATION_SPLIT);
        String portType = keys[0];
        String operationName = keys[1];
        for (SoapOperationConfig operationConfig: configs) {
            if (StringUtils.equalsIgnoreCase(operationConfig.getPortType(), portType) && StringUtils.equalsIgnoreCase(operationConfig.getName(), operationName)) {
                return operationConfig;
            }
        }
        return null;
    }

    @Override
    public List<String> operations(SoapServiceConfig soapServiceConfig) {
        return soapServiceConfig.getSoapOperations().stream()
                .map(config -> config.getPortType() + OPERATION_SPLIT + config.getName())
                .collect(Collectors.toList());
    }

    @Override
    public SoapServiceConfig importConfig(WSDLImportConfig wsdlImportConfig) {
        SoapServiceConfig serviceConfig = new SoapServiceConfig();
        List<SoapOperationConfig> soapOperations = new ArrayList<>();
        WSDLParser parser = new WSDLParser();
        String wsdl = wsdlImportConfig.getWsdlUrl();
        Definitions definitions = parser.parse(wsdl);
        List<Binding> bindings = definitions.getBindings();
        String wsdlDoc = getWsdlDoc(wsdl);
        bindings.forEach(binding -> {
            String bindingName = binding.getName();
            String portType = binding.getType().getLocalPart();
            binding.getOperations().forEach(operation -> {
                SoapOperationConfig soapOperation = new SoapOperationConfig();
                SoapSchemaOperation schemaOperation = new SoapSchemaOperation();

                schemaOperation.setBinding(bindingName);
                soapOperation.setName(operation.getName());
                schemaOperation.setName(operation.getName());

                String inputName = operation.getInput().getName();
                String outputName = operation.getOutput().getName();
                soapOperation.setPortType(portType);
                schemaOperation.setPortType(portType);

                schemaOperation.setInput(inputName);
                schemaOperation.setOutPut(outputName);
                schemaOperation.setWsdl(wsdl);
                soapOperation.setOperationKey(portType + OPERATION_SPLIT + operation.getName());

                String baseDir = definitions.getBaseDir().toString();
                soapOperation.setBaseUrl(StringUtils.substringAfter(baseDir, "://"));
                schemaOperation.setBaseUrl(soapOperation.getBaseUrl());
                soapOperation.setProtocol(HttpProtocol.valueOf(StringUtils.substringBefore(baseDir, "://").toUpperCase()));
                schemaOperation.setProtocol(soapOperation.getProtocol());

                MessageModel inModel = buildModel(definitions.getMessage(inputName), schemaOperation, wsdlDoc);
                MessageModel outModel = buildModel(definitions.getMessage(outputName), schemaOperation, wsdlDoc);
                soapOperation.setInModel(inModel);
                soapOperation.setOutModel(outModel);
                soapOperations.add(soapOperation);
            });
        });
        serviceConfig.setSoapOperations(soapOperations);
        serviceConfig.setWsdl(wsdl);
        return serviceConfig;
    }

    public MessageModel buildModel(Message message, SoapSchemaOperation operationConfig, String wsdlDoc) {
        MessageModel model = new MessageModel();
        SoapSchema soapSchema = new SoapSchema();
        soapSchema.setWsdlDoc(wsdlDoc);
        soapSchema.setOperationConfig(operationConfig);
        model.setModelSchema(soapSchema);
        model.setModelType(BodyType.SOAP);
        Map<String, ModelField> fields = new HashMap<>();
        ModelField rootField = new ModelField();
        buildFieldWithoutRootPath(rootField, message);
        fields.put("root", rootField);
        soapSchema.setFields(fields);
        return model;
    }

    private void buildFieldWithoutRootPath(ModelField parent, Message message) {
        Map<String, ModelField> child = new HashMap<>();
        if (message != null) {
            List<Part> parts = message.getParts();
            if (CollectionUtils.isNotEmpty(parts)) {
                parts.forEach(part -> {
                    Element element = part.getElement();
                    if (element != null) {
                        handleElement(element, parent, child);
                    }
                });
            }
        }
        parent.setFields(child);
    }

    private void handleElement(Element element, ModelField parent, Map<String, ModelField> child) {
        Schema schema = element.getSchema();
        String type;
        if (element.getType() != null) {
            type = element.getType().getLocalPart();
            if (containsComplexType(type, schema) && !containsSimpleType(type, schema)) {
                if (element.getArrayType() == null
                        && (StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "0") || StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "1"))) {
                    parent.setType(FieldType.OBJECT);
                } else {
                    parent.setType(FieldType.ARRAY);
                    parent.setSubType(FieldType.OBJECT);
                }
                buildWithoutRootPath(child, schema, type);
            } else {
                FieldType fieldType = getFieldType(type);
                if (element.getArrayType() == null) {
                    parent.setType(fieldType);
                } else {
                    parent.setType(FieldType.ARRAY);
                    parent.setType(fieldType);
                }
            }
        } else {
            buildByEmbeddedTypeWithoutRootPath(child, element, schema);
        }
    }

    private void buildWithoutRootPath(Map<String, ModelField> parent, Schema schema, String type) {
        ComplexType complexType = schema.getComplexType(type);
        Sequence sequence = complexType.getSequence();
        if (sequence != null) {
            List<SchemaComponent> particles = sequence.getParticles();
            if (CollectionUtils.isNotEmpty(particles)) {
                particles.forEach(p -> {
                    handleSchemaComponent(p, schema, parent);
                });
            }
        }
    }

    private void handleSchemaComponent(SchemaComponent p, Schema schema, Map<String, ModelField> parent) {
        ModelField modelField = new ModelField();
        Element element = (Element) p;
        String name = element.getName();
        String elementType = element.getType().getLocalPart();
        if (containsComplexType(elementType, schema) && !containsSimpleType(elementType, schema)) {
            if (element.getArrayType() == null && (StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "0") || StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "1"))) {
                modelField.setType(FieldType.OBJECT);
            } else {
                modelField.setType(FieldType.ARRAY);
                modelField.setSubType(FieldType.OBJECT);
            }
            Map<String, ModelField> child = new HashMap<>();
            modelField.setFields(child);
            buildWithoutRootPath(child, schema, elementType);
        } else {
            FieldType fieldType = getFieldType(elementType);
            if (element.getArrayType() == null) {
                modelField.setType(fieldType);
            } else {
                modelField.setType(FieldType.ARRAY);
                modelField.setSubType(fieldType);
            }
        }
        parent.put(name, modelField);
    }

    private void buildByEmbeddedTypeWithoutRootPath(Map<String, ModelField> parent, Element element, Schema schema) {
        ComplexType complexType = (ComplexType) element.getEmbeddedType();
        Sequence sequence;
        if (complexType != null && (sequence = (Sequence) complexType.getModel()) != null) {
            List<SchemaComponent> particles = sequence.getParticles();
            String maxOccurs = (String) sequence.getMaxOccurs();
            if (CollectionUtils.isNotEmpty(particles)) {
                particles.forEach(p -> {
                    handleSchemaComponent(p, element, schema, maxOccurs, parent);
                });
            }
        }
    }

    private void handleSchemaComponent(SchemaComponent p, Element element, Schema schema, String maxOccurs, Map<String, ModelField> parent) {
        ModelField modelField = new ModelField();
        Element e = (Element) p;
        String name = e.getName();
        String elementType = e.getType().getLocalPart();
        if (containsComplexType(elementType, schema) && !containsSimpleType(elementType, schema)) {
            if (element.getArrayType() == null
                    && (StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "0") || StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "1"))
                    && (StringUtils.equalsIgnoreCase(maxOccurs, "0") || StringUtils.equalsIgnoreCase(maxOccurs, "1"))) {
                modelField.setType(FieldType.OBJECT);
            } else {
                modelField.setType(FieldType.ARRAY);
                modelField.setSubType(FieldType.OBJECT);
            }
            Map<String, ModelField> child = new HashMap<>();
            modelField.setFields(child);
            buildWithoutRootPath(child, schema, elementType);
        } else {
            FieldType fieldType = getFieldType(elementType);
            if (element.getArrayType() == null
                    && (StringUtils.equalsIgnoreCase(maxOccurs, "0") || StringUtils.equalsIgnoreCase(maxOccurs, "1"))) {
                modelField.setType(fieldType);
            } else {
                modelField.setType(FieldType.ARRAY);
                modelField.setSubType(fieldType);
            }
        }
        parent.put(name, modelField);
    }

    private boolean containsSimpleType(String type, Schema schema) {
        List<SimpleType> simpleTypes = schema.getSimpleTypes();
        if (CollectionUtils.isNotEmpty(simpleTypes)) {
            for (SimpleType simpleType: simpleTypes) {
                if (StringUtils.equalsIgnoreCase(type, simpleType.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsComplexType(String type, Schema schema) {
        List<ComplexType> complexTypes = schema.getComplexTypes();
        if (CollectionUtils.isNotEmpty(complexTypes)) {
            for (ComplexType complexType: complexTypes) {
                if (StringUtils.equalsIgnoreCase(type, complexType.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private FieldType getFieldType(String type) {
        switch (type) {
            case "int":
                return FieldType.INTEGER;
            case "number":
                return FieldType.NUMBER;
            case "string":
                return FieldType.STRING;
            case "boolean":
                return FieldType.BOOLEAN;
        }
        return null;
    }

    private String getWsdlDoc(String url) {
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = new DefaultHttpClient().execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
