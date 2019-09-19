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
import io.terminus.dalaran.component.trigger.soap.model.SoapModel;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.service.soap.SoapOperation;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.predic8.schema.Schema.INT;

public class WSDLUtils {
    public static Definitions buildDefinitions(List<SoapApiInfo> soapApiInfos) {
        Definitions definitions = new Definitions("http://schemas.xmlsoap.org/wsdl", "Dalaran");
        Map<String, SoapModel> models = new HashMap<>();
        List<SoapOperation> operations = new ArrayList<>();
        soapApiInfos.forEach(soapApiInfo -> {
            SoapModel input = soapApiInfo.getInput();
            SoapModel output = soapApiInfo.getOutput();
            models.put(input.getName(), input);
            models.put(output.getName(), output);
            SoapOperation operation = new SoapOperation();
            operation.setInput(input.getName());
            operation.setOutput(output.getName());
            operation.setName(soapApiInfo.getName());
            operations.add(operation);
        });

        /**
         * schema + message
         */
        Schema schema = new Schema("http://schemas.xmlsoap.org/wsdl");
        definitions.addSchema(schema);
        schema.setDefinitions(definitions);
        models.forEach((name, model) -> {
            Element element = schema.newElement(name);
//            element.setType();
            DalaranModelSchema modelSchema = model.getSchema();
            ModelField modelField = modelSchema.getFields().get(MapperConstants.MODEL_ROOT);
            buildTypes(element, modelField, schema);
            Message message = definitions.newMessage(name);
//            message.newPart(name, element);
            Part part = message.newPart(name, element);
            part.setElementPN(new PrefixedName("tns", name));
            part.setParent(message);
        });

        /**
         * port type + binding
         */
        PortType pt = definitions.newPortType(SoapConstants.PORT_TYPE);
        soapApiInfos.forEach(apiInfo -> {
            Operation op = pt.newOperation(apiInfo.getName());
            op.newInput(apiInfo.getInput().getName()).setMessage(definitions.getMessage(apiInfo.getInput().getName()));
            op.newOutput(apiInfo.getOutput().getName()).setMessage(definitions.getMessage(apiInfo.getOutput().getName()));
        });

        /**
         *
         */
        Port port = definitions.newService(SoapConstants.SERVICE_NAME).newPort(SoapConstants.SERVICE_PORT);
        Binding binding = port.newBinding(SoapConstants.BINDING);
        binding.setType(pt);
        SOAPBinding soapBinding = binding.newSOAP11Binding();
        soapBinding.setBinding(binding);
        soapApiInfos.forEach(apiInfo -> {
            BindingOperation bindingOperation = binding.newBindingOperation(apiInfo.getName());
            SOAPOperation soapOperation = bindingOperation.newSOAP11Operation();
            soapOperation.setName(apiInfo.getName());
            soapOperation.setSoapAction(SoapConstants.SERVER_ADDRESS + apiInfo.getPath());
            BindingInput bindingInput = bindingOperation.newInput();
            bindingInput.setName(apiInfo.getInput().getName());
            SOAPBody inputBody = bindingInput.newSOAP11Body();
            inputBody.setUse("literal");
            BindingOutput bindingOutput = bindingOperation.newOutput();
            bindingOutput.setName(apiInfo.getOutput().getName());
            SOAPBody outputBody = bindingOutput.newSOAP11Body();
            outputBody.setUse("literal");
        });

        /**
         * service
         */
        port.newSOAP11Address(SoapConstants.SERVER_ADDRESS);
        return definitions;
    }

    private static void buildTypes(Element element, ModelField modelField, Schema schema) {
        if (MapUtils.isEmpty(modelField.getFields())) {
            return;
        }

        String maxOccurs = "1";
        if (modelField.getType() == FieldType.ARRAY) {
            maxOccurs = SoapConstants.UNBOUNDED;
        }
        element.setMaxOccurs(maxOccurs);
        Sequence sequence = element.newComplexType().newSequence();

        modelField.getFields().forEach((name, field) -> {
            Element e = sequence.newElement(name);
            if (field.getType() == FieldType.ARRAY) {
                e.setMaxOccurs(SoapConstants.UNBOUNDED);
            } else {
                e.setMaxOccurs("1");
            }
            buildChildrenType(name, field, schema, e, sequence);
        });
    }

    private static void buildChildrenType(String name, ModelField modelField, Schema schema, Element parent, Sequence parentSequence) {
        if (modelField.getType() != FieldType.OBJECT && !(modelField.getType() == FieldType.ARRAY && modelField.getSubType() == FieldType.OBJECT)) {
            parent.setType(new QName("http://www.w3.org/2001/XMLSchema", getFieldType(modelField.getType()), "xsd"));
            parent.setParent(parentSequence);
            return;
        }

        parent.setType(new QName("", name, "tns"));
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
