package io.terminus.dalaran.service.soap;

import com.predic8.schema.*;
import com.predic8.wsdl.*;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.annotation.ServiceConnector;
import io.terminus.dalaran.model.*;
import io.terminus.dalaran.model.schema.SoapSchema;
import io.terminus.dalaran.model.schema.SoapSchemaOperation;
import io.terminus.dalaran.model.soap.model.SoapOperationConfig;
import io.terminus.dalaran.service.soap.model.SchemaModel;
import okhttp3.*;
import org.apache.camel.Exchange;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/5/27
 */
@ServiceConnector(value = "soap-connector", importConfigType = WSDLImportConfig.class, serviceConfigType = SoapServiceConfig.class)
public class SoapService implements DalaranService<WSDLImportConfig, SoapServiceConfig, SoapOperationConfig> {

    private static final String OPERATION_SPLIT = "::";

    private static final String HTTP_URI = "%s4://%s";

    @Override
    public void configure(ProcessorDefinition route, SoapOperationConfig soapOperationConfig) {
        String uri = String.format(HTTP_URI, "http", soapOperationConfig.getLocation());
        if (StringUtils.contains(uri, "?")) {
            uri = uri + "&bridgeEndpoint=true";
        } else {
            uri = uri + "?bridgeEndpoint=true";
        }
        route.setHeader(Exchange.HTTP_METHOD, Builder.constant(HttpMethod.POST));
        route.setHeader(Exchange.CONTENT_TYPE, Builder.constant("text/xml; charset=utf-8"));
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
        WSDLParser parser = new WSDLParser();
        String wsdl = wsdlImportConfig.getWsdlUrl();
        Definitions definitions = new Definitions();
        String username = wsdlImportConfig.getUsername();
        String password = wsdlImportConfig.getPassword();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            try {
                InputStream wsdlDoc = getWSDLDoc(wsdl, username, password);
                definitions = parser.parse(wsdlDoc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            definitions = parser.parse(wsdl);
        }
        List<Binding> bindings = definitions.getBindings();
        Map<String, List<SoapOperationConfig>> soapOperationConfigMap = new HashMap<>();
        String targetNamespace = definitions.getTargetNamespace();
        bindings.forEach(binding -> {
            String bindingName = binding.getName();
            String portType = binding.getType().getLocalPart();
            binding.getOperations().forEach(operation -> {
                SoapOperationConfig operationConfig = new SoapOperationConfig();
                String operationName = operation.getName();
                String operationKey = operationName + OPERATION_SPLIT + portType  + OPERATION_SPLIT + bindingName;
                operationConfig.setBinding(bindingName);
                operationConfig.setOperation(operationName);
                operationConfig.setPortType(portType);
                operationConfig.setOperationKey(operationKey);
                operationConfig.setTargetNamespace(targetNamespace);
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
                String location = StringUtils.substringAfter(port.getAddress().getLocation(), "://");
                if (StringUtils.isNotBlank(wsdlImportConfig.getUsername()) && StringUtils.isNotBlank(wsdlImportConfig.getPassword())) {
                    location = location + "&authMethod=Basic&authUsername=" + wsdlImportConfig.getUsername() + "&authPassword=" + wsdlImportConfig.getPassword();
                }
                String operationUrl = location;
                operations.forEach(operation -> {
                    try {
                        SoapOperationConfig newOperation = (SoapOperationConfig) BeanUtils.cloneBean(operation);
                        newOperation.setLocation(operationUrl);
                        newOperation.setProtocol(HttpProtocol.valueOf(StringUtils.substringBefore(port.getAddress().getLocation(), "://").toUpperCase()));
                        newOperation.setServicePort(port.getName());
                        newOperation.setOperationKey(operation.getOperationKey());
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
        Definitions definitions = new Definitions();
        String username = wsdlImportConfig.getUsername();
        String password = wsdlImportConfig.getPassword();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            try {
                InputStream wsdlDoc = getWSDLDoc(wsdl, username, password);
                definitions = parser.parse(wsdlDoc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            definitions = parser.parse(wsdl);
        }
        SchemaModel schemaModel = getSchemaModel(definitions.getLocalTypes());
        Map<String, SoapOperation> operationMap = buildOperations(definitions);
        SoapSchemaOperation schemaOperation = new SoapSchemaOperation();
        SoapOperation soapOperation = operationMap.get(StringUtils.substringBeforeLast(operationConfig.getOperationKey(), OPERATION_SPLIT));

        String inputName = soapOperation.getInput();
        String outputName = soapOperation.getOutput();
        schemaOperation.setTargetNamespace(operationConfig.getTargetNamespace());

        MessageModel inModel = buildModel(definitions.getMessage(inputName), schemaOperation, schemaModel);
        MessageModel outModel = buildModel(definitions.getMessage(outputName), schemaOperation, schemaModel);

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
                operations.put(name + OPERATION_SPLIT + portName, soapOperation);
            });
        });

        List<Binding> bindings = definitions.getBindings();
        bindings.forEach(binding -> {
            String portType =  binding.getPortType().getName();
            binding.getOperations().forEach(operation -> {

            });
        });

        return operations;
    }

    public MessageModel buildModel(Message message, SoapSchemaOperation operationConfig, SchemaModel schemaModel) {
        MessageModel<SoapSchema> model = new MessageModel<>();
        SoapSchema soapSchema = new SoapSchema();
        try {
            SoapSchemaOperation schemaOperation = new SoapSchemaOperation();
            BeanUtils.copyProperties(schemaOperation, operationConfig);
            soapSchema.setOperationConfig(schemaOperation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.setModelSchema(soapSchema);
        model.setModelType("SOAP");
        Map<String, ModelField> fields = new HashMap<>();
        ModelField rootField = new ModelField();
        buildFieldWithoutRootPath(rootField, message, schemaModel);
        fields.put(DalaranConstants.MODEL_ROOT, rootField);
        soapSchema.setFields(fields);
        return model;
    }

    private void buildFieldWithoutRootPath(ModelField parent, Message message, SchemaModel schemaModel) {
        Map<String, ModelField> child = new HashMap<>();
        if (message != null) {
            List<Part> parts = message.getParts();
            if (CollectionUtils.isNotEmpty(parts)) {
                parent.setType(FieldType.OBJECT);
                parts.forEach(part -> {
                    Element element = part.getElement();
                    if (element != null) {
                        handleElement(element, child, schemaModel);
                    }
                });
            }
        }
        parent.setFields(child);
    }

    private void handleElement(Element element, Map<String, ModelField> child, SchemaModel schemaModel) {
        Schema schema = element.getSchema();
        ModelField field = new ModelField();
        String type;
        if (element.getType() != null) {
            type = element.getType().getLocalPart();
            if (schemaModel.getComplexTypes().containsKey(type) && !schemaModel.getSimpleTypes().containsKey(type)) {
                if (element.getArrayType() == null
                        && (StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "0") || StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "1"))) {
                    field.setType(FieldType.OBJECT);
                } else {
                    field.setType(FieldType.ARRAY);
                    field.setSubType(FieldType.OBJECT);
                }
                buildWithoutRootPath(field, schema, type, schemaModel);
            } else {
                FieldType fieldType = getFieldType(type);
                if (element.getArrayType() == null
                        && (StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "0") || StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "1"))) {
                    field.setType(fieldType);
                } else {
                    field.setType(FieldType.ARRAY);
                    field.setType(fieldType);
                }
            }
        } else {
            buildByEmbeddedTypeWithoutRootPath(field, element, schema, schemaModel);
        }
        child.put(element.getName(), field);
    }

    private void buildWithoutRootPath(ModelField field, Schema schema, String type, SchemaModel schemaModel) {
        ComplexType complexType = schemaModel.getComplexTypes().get(type);
        Sequence sequence = complexType.getSequence();
        if (sequence != null) {
            List<SchemaComponent> particles = sequence.getParticles();
            if (CollectionUtils.isNotEmpty(particles)) {
                Map<String, ModelField> current = new HashMap<>();
                particles.forEach(p -> {
                    handleSchemaComponent(p, schema, current, schemaModel);
                });
                field.setFields(current);
            }
        }
    }

    private void handleSchemaComponent(SchemaComponent p, Schema schema, Map<String, ModelField> current, SchemaModel schemaModel) {
        ModelField modelField = new ModelField();
        Element element = (Element) p;
        String name = element.getName();
        String elementType = element.getType().getLocalPart();
        if (schemaModel.getComplexTypes().containsKey(elementType) && !schemaModel.getSimpleTypes().containsKey(elementType)) {
            if (element.getArrayType() == null && (StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "0") || StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "1"))) {
                modelField.setType(FieldType.OBJECT);
            } else {
                modelField.setType(FieldType.ARRAY);
                modelField.setSubType(FieldType.OBJECT);
            }
            buildWithoutRootPath(modelField, schema, elementType, schemaModel);
        } else {
            FieldType fieldType = getFieldType(elementType);
            if (element.getArrayType() == null
                    && (StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "0") || StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "1"))) {
                modelField.setType(fieldType);
            } else {
                modelField.setType(FieldType.ARRAY);
                modelField.setSubType(fieldType);
            }
        }
        current.put(name, modelField);
    }

    private void buildByEmbeddedTypeWithoutRootPath(ModelField field, Element element, Schema schema, SchemaModel schemaModel) {
        ComplexType complexType = (ComplexType) element.getEmbeddedType();
        Sequence sequence;
        if (complexType != null && (sequence = (Sequence) complexType.getModel()) != null) {
            List<SchemaComponent> particles = sequence.getParticles();
            String maxOccurs = sequence.getMaxOccurs().toString();
            if (StringUtils.equalsIgnoreCase(maxOccurs, "0") || StringUtils.equalsIgnoreCase(maxOccurs, "1")) {
                field.setType(FieldType.OBJECT);
            } else {
                field.setType(FieldType.ARRAY);
                field.setSubType(FieldType.OBJECT);
            }
            if (CollectionUtils.isNotEmpty(particles)) {
                Map<String, ModelField> current = new HashMap<>();
                particles.forEach(p -> {
                    handleSchemaComponent(p, element, schema, maxOccurs, current, schemaModel);
                });
                field.setFields(current);
            }
        }
    }

    private void handleSchemaComponent(SchemaComponent p, Element element, Schema schema, String maxOccurs, Map<String, ModelField> parent, SchemaModel schemaModel) {
        ModelField modelField = new ModelField();
        if (p instanceof Any) {
            return;
        }
        Element e = (Element) p;
        String name = e.getName();
        if (StringUtils.isBlank(name)) {
            return;
        }
        if (e.getType() == null) {
            parent.put(name, modelField);
            return;
        }
        if (StringUtils.isNotBlank(e.getMaxOccurs())) {
            maxOccurs = e.getMaxOccurs();
        }
        String elementType = e.getType().getLocalPart();
        if (schemaModel.getComplexTypes().containsKey(elementType) && !schemaModel.getSimpleTypes().containsKey(elementType)) {
            if (element.getArrayType() == null
                    && (StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "0") || StringUtils.equalsIgnoreCase(element.getMaxOccurs(), "1"))
                    && (StringUtils.equalsIgnoreCase(maxOccurs, "0") || StringUtils.equalsIgnoreCase(maxOccurs, "1"))) {
                modelField.setType(FieldType.OBJECT);
            } else {
                modelField.setType(FieldType.ARRAY);
                modelField.setSubType(FieldType.OBJECT);
            }
            buildWithoutRootPath(modelField, schema, elementType, schemaModel);
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

    private SchemaModel getSchemaModel(Types types) {
        Map<String, ComplexType> complexTypes = new HashMap<>();
        Map<String, SimpleType> simpleTypes = new HashMap<>();
        types.getSchemas().forEach(schema -> {
            schema.getComplexTypes().forEach(complexType -> {
                complexTypes.put(complexType.getName(), complexType);
            });
            schema.getSimpleTypes().forEach(simpleType -> {
                simpleTypes.put(simpleType.getName(), simpleType);
            });
        });
        return new SchemaModel(complexTypes, simpleTypes);
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

    private InputStream getWSDLDoc(String url, String user, String password) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().authenticator(new Authenticator() {
            @Nullable
            @Override
            public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                String credentials =  Credentials.basic(user, password);
                return response.request().newBuilder().header("Authorization", credentials).build();
            }
        }).build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().byteStream();
    }
}
