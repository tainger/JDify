package io.terminus.dalaran.service.soap;

import com.predic8.schema.*;
import com.predic8.wsdl.*;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.annotation.ServiceConnector;
import io.terminus.dalaran.core.component.model.ServiceOperationModel;
import io.terminus.dalaran.model.*;
import io.terminus.dalaran.model.component.ServiceOperation;
import io.terminus.dalaran.model.converter.soap.model.SoapOperationConfig;
import io.terminus.dalaran.model.converter.soap.model.SoapSchemaOperation;
import io.terminus.dalaran.model.schema.SoapSchema;
import org.apache.camel.Exchange;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

    private static final String OPERATION_SPLIT = "::";

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
        List<SoapOperationConfig> configs = soapServiceConfig.getConfigs();
        for (SoapOperationConfig operationConfig : configs) {
            if (StringUtils.equals(operationConfig.getOperationKey(), operationKey)) {
                return operationConfig;
            }
        }
        return null;
    }

    @Override
    public List<SoapOperationConfig> operations(SoapServiceConfig soapServiceConfig) {
        return soapServiceConfig.getConfigs();
    }

    @Override
    public SoapServiceConfig importConfig(WSDLImportConfig wsdlImportConfig) {
        SoapServiceConfig serviceConfig = new SoapServiceConfig();
        List<SoapOperationConfig> soapOperations = new ArrayList<>();
        WSDLParser parser = new WSDLParser();
        String wsdl = wsdlImportConfig.getWsdlUrl();
        Definitions definitions = parser.parse(wsdl);
        List<Binding> bindings = definitions.getBindings();
        Map<String, List<SoapOperationConfig>> soapOperationConfigMap = new HashMap<>();
        Map<String, SoapOperation> operationMap = buildOperations(definitions);
        bindings.forEach(binding -> {
            String bindingName = binding.getName();
            String portType = binding.getType().getLocalPart();
            binding.getOperations().forEach(operation -> {
                SoapOperationConfig operationConfig = new SoapOperationConfig();
                String operationName = operation.getName();
                String operationKey = bindingName + OPERATION_SPLIT + portType + OPERATION_SPLIT + operationName;
                String subKey = portType + OPERATION_SPLIT + operationName;
                SoapOperation soapOperation = operationMap.get(subKey);
                operationConfig.setBinding(bindingName);
                operationConfig.setOperation(operationName);
                operationConfig.setPortType(portType);
                operationConfig.setOperationKey(operationKey);
                soapOperations.add(operationConfig);
                if (soapOperationConfigMap.containsKey(bindingName)) {
                    soapOperationConfigMap.get(bindingName).add(operationConfig);
                } else {
                    List<SoapOperationConfig> operationConfigs = new ArrayList<>();
                    operationConfigs.add(operationConfig);
                    soapOperationConfigMap.put(bindingName, operationConfigs);
                }
            });
        });
        List<SoapOperationConfig> operationList = new ArrayList<>();
        definitions.getServices().forEach(service -> {
            service.getPorts().forEach(port -> {
                List<SoapOperationConfig> operations = soapOperationConfigMap.get(port.getBindingPN().getLocalName());
                String baseUrl = StringUtils.substringAfter(port.getAddress().getLocation(), "://");
                if (wsdlImportConfig.getUsername() != null && wsdlImportConfig.getPassword() != null) {
                    baseUrl = baseUrl + "&authMethod=Basic&authUsername=" + wsdlImportConfig.getUsername() + "&authPassword=" + wsdlImportConfig.getPassword();
                }
                String operationUrl = baseUrl;
                operations.forEach(operation -> {
                    try {
                        SoapOperationConfig newOperation = (SoapOperationConfig) BeanUtils.cloneBean(operation);
                        newOperation.setBaseUrl(operationUrl);
                        newOperation.setProtocol(HttpProtocol.valueOf(StringUtils.substringBefore(port.getAddress().getLocation(), "://").toUpperCase()));
                        newOperation.setServicePort(port.getName());
                        String operationKey = port.getName() + OPERATION_SPLIT + operation.getOperationKey();
                        newOperation.setOperationKey(operationKey);
                        operationList.add(newOperation);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        });
        serviceConfig.setConfigs(operationList);
        serviceConfig.setWsdl(wsdl);
        return serviceConfig;
    }

    @Override
    public ServiceOperationModel buildOperationModel(WSDLImportConfig wsdlImportConfig, SoapOperationConfig operationConfig) {
        WSDLParser parser = new WSDLParser();
        String wsdl = wsdlImportConfig.getWsdlUrl();
        Definitions definitions = parser.parse(wsdl);
        String wsdlDoc = getWsdlDoc(wsdl);
        Map<String, SoapOperation> operationMap = buildOperations(definitions);
        SoapSchemaOperation schemaOperation = new SoapSchemaOperation();
        SoapOperation soapOperation = operationMap.get(StringUtils.substringAfter(StringUtils.substringAfter(operationConfig.getOperationKey(), OPERATION_SPLIT), OPERATION_SPLIT));

        schemaOperation.setBinding(operationConfig.getBinding());
        schemaOperation.setName(operationConfig.getOperation());

        String inputName = soapOperation.getInput();
        String outputName = soapOperation.getOutput();
        schemaOperation.setPortType(operationConfig.getPortType());
        schemaOperation.setInput(inputName);
        schemaOperation.setOutPut(outputName);
        schemaOperation.setWsdl(wsdl);

        schemaOperation.setBaseUrl(operationConfig.getBaseUrl());
        schemaOperation.setProtocol(operationConfig.getProtocol());

        MessageModel inModel = buildModel(definitions.getMessage(inputName), schemaOperation, wsdlDoc, inputName);
        MessageModel outModel = buildModel(definitions.getMessage(outputName), schemaOperation, wsdlDoc, outputName);

        return new ServiceOperationModel(inModel, inputName, outModel, outputName);
    }

    private Map<String, SoapOperation> buildOperations(Definitions definitions) {
        Map<String, SoapOperation> operations = new HashMap<>();
        List<PortType> portTypes = definitions.getPortTypes();
        portTypes.forEach(portType -> {
            String portName = portType.getName();
            portType.getOperations().forEach(operation -> {
                SoapOperation soapOperation = new SoapOperation();
                String name = operation.getName();

                soapOperation.setName(name);
                soapOperation.setPortType(portName);
                String input = operation.getInput().getName();
                if (StringUtils.isBlank(input)) {
                    input = operation.getInput().getMessagePrefixedName().getLocalName();
                }
                soapOperation.setInput(input);

                String output = operation.getOutput().getName();
                if (StringUtils.isBlank(output)) {
                    output = operation.getOutput().getMessagePrefixedName().getLocalName();
                }
                soapOperation.setOutput(output);
                operations.put(portName + OPERATION_SPLIT + name, soapOperation);
            });
        });

        return operations;
    }

    public MessageModel buildModel(Message message, SoapSchemaOperation operationConfig, String wsdlDoc, String modelRoot) {
        MessageModel model = new MessageModel();
        SoapSchema soapSchema = new SoapSchema();
        try {
            SoapSchemaOperation schemaOperation = new SoapSchemaOperation();
            BeanUtils.copyProperties(schemaOperation, operationConfig);
            schemaOperation.setModelRoot(modelRoot);
            soapSchema.setOperationConfig(schemaOperation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        soapSchema.setWsdlDoc(wsdlDoc);
        model.setModelSchema(soapSchema);
        model.setModelType(BodyType.SOAP);
        Map<String, ModelField> fields = new HashMap<>();
        ModelField rootField = new ModelField();
        buildFieldWithoutRootPath(rootField, message);
        fields.put(DalaranConstants.MODEL_ROOT, rootField);
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
            String maxOccurs = sequence.getMaxOccurs().toString();
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
            for (SimpleType simpleType : simpleTypes) {
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
            for (ComplexType complexType : complexTypes) {
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
            case "string":
                return FieldType.STRING;
            case "boolean":
                return FieldType.BOOLEAN;
        }
        return FieldType.STRING;
    }

    private String getWsdlDoc(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
