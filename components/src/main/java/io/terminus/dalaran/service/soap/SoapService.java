package io.terminus.dalaran.service.soap;

import com.predic8.schema.*;
import com.predic8.wsdl.*;
import io.terminus.dalaran.component.processor.soap.DalaranSoapProcessor;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.annotation.ServiceConnector;
import io.terminus.dalaran.core.model.BodyType;
import io.terminus.dalaran.core.model.FieldType;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.model.ModelField;
import io.terminus.dalaran.core.model.schema.XMLSchema;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public void configure(ProcessorDefinition route, SoapOperationConfig soapOperationConfig) {
        WSDLParser parser = new WSDLParser();
        Definitions definitions = parser.parse(soapOperationConfig.getWsdl());
        DalaranSoapProcessor processor = new DalaranSoapProcessor(soapOperationConfig, definitions);
        route.unmarshal().json(JsonLibrary.Fastjson).process(processor).marshal().json(JsonLibrary.Fastjson);
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

        bindings.forEach(binding -> {
            String bindingName = binding.getName();
            binding.getOperations().forEach(operation -> {
                SoapOperationConfig soapOperation = new SoapOperationConfig();
                soapOperation.setBinding(bindingName);
                soapOperation.setName(operation.getName());
                String inputName = operation.getInput().getName();
                String outputName = operation.getOutput().getName();
                soapOperation.setPortType(inputName);
                soapOperation.setInput(inputName);
                soapOperation.setOutPut(outputName);
                soapOperation.setWsdl(wsdl);
                MessageModel inModel = buildModel(definitions.getMessage(inputName));
                MessageModel outModel = buildModel(definitions.getMessage(outputName));
                soapOperation.setInModel(inModel);
                soapOperation.setOutModel(outModel);
                soapOperations.add(soapOperation);
            });
        });
        serviceConfig.setSoapOperations(soapOperations);
        serviceConfig.setWsdl(wsdl);
        return serviceConfig;
    }

    public MessageModel buildModel(Message message) {
        MessageModel model = new MessageModel();
        XMLSchema xmlSchema = new XMLSchema();
        model.setModelSchema(xmlSchema);
        model.setModelType(BodyType.XML);
        Map<String, ModelField> fields = new HashMap<>();
        ModelField rootField = new ModelField();
        buildFieldWithoutRootPath(rootField, message);
        fields.put("root", rootField);
        xmlSchema.setFields(fields);
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
                            buildByEmbeddedTypeWithoutRootPath(child, element, schema, parent);
                        }
                    }
                });
            }
        }
        parent.setFields(child);
    }

    private void buildWithoutRootPath(Map<String, ModelField> parent, Schema schema, String type) {
        ComplexType complexType = schema.getComplexType(type);
        Sequence sequence = complexType.getSequence();
        if (sequence != null) {
            List<SchemaComponent> particles = sequence.getParticles();
            if (CollectionUtils.isNotEmpty(particles)) {
                particles.forEach(p -> {
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
                });
            }
        }
    }

    private void buildByEmbeddedTypeWithoutRootPath(Map<String, ModelField> parent, Element element, Schema schema, ModelField model) {
        ComplexType complexType = (ComplexType) element.getEmbeddedType();
        if (complexType != null) {
            Sequence sequence = (Sequence) complexType.getModel();
            if (sequence != null) {
                List<SchemaComponent> particles = sequence.getParticles();
                String maxOccurs = (String) sequence.getMaxOccurs();
                if (CollectionUtils.isNotEmpty(particles)) {
                    particles.forEach(p -> {
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
                    });
                }
            }
        }
    }

    private void buildField(ModelField parent, Message message) {
        Map<String, ModelField> child = new HashMap<>();
        if (message != null) {
            List<Part> parts = message.getParts();
            if (CollectionUtils.isNotEmpty(parts)) {
                parts.forEach(part -> {
                    ModelField modelField = new ModelField();
                    Element element = part.getElement();
                    if (element != null) {
                        Schema schema = element.getSchema();
                        String name = element.getName();
                        String type;
                        if (element.getType() != null) {
                            type = element.getType().getLocalPart();
                            if (containsComplexType(type, schema) && !containsSimpleType(type, schema)) {
                                if (element.getArrayType() == null) {
                                    modelField.setType(FieldType.OBJECT);
                                } else {
                                    modelField.setType(FieldType.ARRAY);
                                    modelField.setSubType(FieldType.OBJECT);
                                }
                                build(modelField, schema, type);
                            } else {
                                FieldType fieldType = getFieldType(type);
                                if (element.getArrayType() == null) {
                                    modelField.setType(fieldType);
                                } else {
                                    modelField.setType(FieldType.ARRAY);
                                    modelField.setType(fieldType);
                                }
                            }
                        } else {
                            buildByEmbeddedType(modelField, element, schema);
                        }
                        child.put(name, modelField);
                    }
                });
            }
        }
        parent.setFields(child);
    }

    private void buildByEmbeddedType(ModelField parent, Element element, Schema schema) {
        Map<String, ModelField> child = new HashMap<>();
        ComplexType complexType = (ComplexType) element.getEmbeddedType();
        if (complexType != null) {
            Sequence sequence = (Sequence) complexType.getModel();
            if (sequence != null) {
                List<SchemaComponent> particles = sequence.getParticles();
                if (CollectionUtils.isNotEmpty(particles)) {
                    particles.forEach(p -> {
                        ModelField modelField = new ModelField();
                        Element e = (Element) p;
                        String name = e.getName();
                        String elementType = e.getType().getLocalPart();
                        if (containsComplexType(elementType, schema) && !containsSimpleType(elementType, schema)) {
                            if (element.getArrayType() == null) {
                                modelField.setType(FieldType.OBJECT);
                            } else {
                                modelField.setType(FieldType.ARRAY);
                                modelField.setSubType(FieldType.OBJECT);
                            }
                            build(modelField, schema, elementType);
                        } else {
                            FieldType fieldType = getFieldType(elementType);
                            if (element.getArrayType() == null) {
                                modelField.setType(fieldType);
                            } else {
                                modelField.setType(FieldType.ARRAY);
                                modelField.setSubType(fieldType);
                            }
                        }
                        child.put(name, modelField);
                    });
                }
            }
        }
        parent.setFields(child);
    }

    private void build(ModelField field, Schema schema, String type) {
        Map<String, ModelField> child = new HashMap<>();
        ComplexType complexType = schema.getComplexType(type);
        Sequence sequence = complexType.getSequence();
        if (sequence != null) {
            List<SchemaComponent> particles = sequence.getParticles();
            if (CollectionUtils.isNotEmpty(particles)) {
                particles.forEach(p -> {
                    ModelField modelField = new ModelField();
                    Element element = (Element) p;
                    String name = element.getName();
                    String elementType = element.getType().getLocalPart();
                    if (containsComplexType(elementType, schema) && !containsSimpleType(elementType, schema)) {
                        if (element.getArrayType() == null) {
                            modelField.setType(FieldType.OBJECT);
                        } else {
                            modelField.setType(FieldType.ARRAY);
                            modelField.setSubType(FieldType.OBJECT);
                        }
                        build(modelField, schema, elementType);
                    } else {
                        FieldType fieldType = getFieldType(elementType);
                        if (element.getArrayType() == null) {
                            modelField.setType(fieldType);
                        } else {
                            modelField.setType(FieldType.ARRAY);
                            modelField.setSubType(fieldType);
                        }
                    }
                    child.put(name, modelField);
                });
            }
        }
        field.setFields(child);
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

    private String lowerFirstChar(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
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
}
