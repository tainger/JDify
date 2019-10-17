package io.terminus.dalaran.component.trigger.soap.utils;

import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.schema.Sequence;
import com.predic8.wsdl.*;
import com.predic8.wsdl.soap11.SOAPBinding;
import com.predic8.wsdl.soap11.SOAPBody;
import com.predic8.wsdl.soap11.SOAPOperation;
import com.predic8.xml.util.PrefixedName;
import groovy.xml.QName;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import io.terminus.dalaran.component.trigger.soap.model.SoapApiInfo;
import io.terminus.dalaran.component.trigger.soap.model.SoapConstants;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WSDLUtils {

    private static final String TNS = "http://schemas.xmlsoap.org/wsdl";

    private static final String XSD = "http://www.w3.org/2001/XMLSchema";

    private static final String OPERATION_SPLIT = "::";

    public static Definitions buildDefinitions(List<SoapApiInfo> soapApiInfos, String runtimeLocation) {
        Definitions definitions = new Definitions(TNS, "Dalaran");
        Map<String, MessageModel> models = new HashMap<>();
        soapApiInfos.forEach(soapApiInfo -> {
            MessageModel input = soapApiInfo.getInput();
            MessageModel output = soapApiInfo.getOutput();
            models.put(input.getName(), input);
            models.put(output.getName(), output);
        });

        /**
         * schema + message
         */
        Schema schema = new Schema(TNS);
        definitions.addSchema(schema);
        schema.setDefinitions(definitions);
        models.forEach((name, model) -> {
            DalaranModelSchema modelSchema = model.getModelSchema();
            ModelField modelField = modelSchema.getFields().get(MapperConstants.MODEL_ROOT);
            Element element = buildTypes(modelField, schema);
            Message message = definitions.newMessage(name);
            Part part = message.newPart(name, element);
            part.setElementPN(new PrefixedName("tns", name));
            part.setParent(message);
        });

        /**
         * port type + binding
         */
        soapApiInfos.forEach(apiInfo -> {
            String apiName = apiInfo.getName().trim();
            PortType pt = definitions.newPortType(SoapConstants.PORT_TYPE + OPERATION_SPLIT + apiName);
            Operation op = pt.newOperation(apiName);
            op.newInput(apiInfo.getInput().getName()).setMessage(definitions.getMessage(apiInfo.getInput().getName()));
            op.newOutput(apiInfo.getOutput().getName()).setMessage(definitions.getMessage(apiInfo.getOutput().getName()));

            Port port = definitions.newService(SoapConstants.SERVICE_NAME + OPERATION_SPLIT + apiName).newPort(SoapConstants.SERVICE_PORT + OPERATION_SPLIT + apiName);
            Binding binding = port.newBinding(SoapConstants.BINDING + OPERATION_SPLIT + apiName);
            binding.setType(pt);
            SOAPBinding soapBinding = binding.newSOAP11Binding();
            soapBinding.setBinding(binding);

            BindingOperation bindingOperation = binding.newBindingOperation(apiName);
            SOAPOperation soapOperation = bindingOperation.newSOAP11Operation();
            soapOperation.setName(apiName);
            soapOperation.setSoapAction(runtimeLocation + apiInfo.getPath());
            BindingInput bindingInput = bindingOperation.newInput();
            bindingInput.setName(apiInfo.getInput().getName());
            SOAPBody inputBody = bindingInput.newSOAP11Body();
            inputBody.setUse("literal");
            BindingOutput bindingOutput = bindingOperation.newOutput();
            bindingOutput.setName(apiInfo.getOutput().getName());
            SOAPBody outputBody = bindingOutput.newSOAP11Body();
            outputBody.setUse("literal");
            port.newSOAP11Address(runtimeLocation + apiInfo.getPath());
        });
        return definitions;
    }

    private static Element buildTypes(ModelField modelField, Schema schema) {
        if (MapUtils.isEmpty(modelField.getFields())) {
            return null;
        }
        String currentName = "";
        ModelField currentField = new ModelField();
        for (Map.Entry<String, ModelField> entry: modelField.getFields().entrySet()) {
            currentName = entry.getKey();
            currentField = entry.getValue();
        }
        Element element = schema.newElement(currentName);

        String maxOccurs = "1";
        if (currentField.getType() == FieldType.ARRAY) {
            maxOccurs = SoapConstants.UNBOUNDED;
        }
        element.setMaxOccurs(maxOccurs);
        Sequence sequence = element.newComplexType().newSequence();
        sequence.setParent(element);

        currentField.getFields().forEach((name, field) -> {
            Element e = sequence.newElement(name);
            if (field.getType() == FieldType.ARRAY) {
                e.setMaxOccurs(SoapConstants.UNBOUNDED);
            } else {
                e.setMaxOccurs("1");
            }
            buildChildrenType(name, field, schema, e, sequence);
        });
        return element;
    }

    private static void buildChildrenType(String name, ModelField modelField, Schema schema, Element parent, Sequence parentSequence) {
        if (modelField.getType() != FieldType.OBJECT && !(modelField.getType() == FieldType.ARRAY && modelField.getSubType() == FieldType.OBJECT)) {
            parent.setType(new QName(XSD, getFieldType(modelField.getType()), "xsd"));
            parent.setParent(parentSequence);
            return;
        }

        parent.setType(new QName(TNS, name, "tns"));
        parent.setParent(parentSequence);
        Sequence sequence = schema.newComplexType(name).newSequence();
        if (MapUtils.isEmpty(modelField.getFields())) {
            return;
        }
        modelField.getFields().forEach((fieldName, field) -> {
            Element e = sequence.newElement(fieldName);
            if (field.getType() == FieldType.ARRAY) {
                e.setMaxOccurs(SoapConstants.UNBOUNDED);
            } else {
                e.setMaxOccurs("1");
            }
            buildChildrenType(fieldName, field, schema, e, sequence);
        });
    }

    private static String getFieldType(FieldType type) {
        switch (String.valueOf(type)) {
            case "INTEGER":
                return "int";
            case "STRING":
                return "string";
            case "BOOLEAN":
                return "boolean";
        }
        return "string";
    }
}
