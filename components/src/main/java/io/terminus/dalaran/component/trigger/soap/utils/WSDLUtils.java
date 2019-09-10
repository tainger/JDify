package io.terminus.dalaran.component.trigger.soap.utils;

import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.schema.Sequence;
import com.predic8.wsdl.*;
import com.predic8.wsdl.soap11.SOAPBody;
import com.predic8.wsdl.soap11.SOAPOperation;
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

public class WSDLUtils {

    public static Definitions buildDefinitions(List<SoapApiInfo> soapApiInfos) {
        Definitions definitions = new Definitions();
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
        Schema schema = new Schema();
        models.forEach((name, model) -> {
            Element element = schema.newElement(name);
            DalaranModelSchema modelSchema = model.getSchema();
            ModelField modelField = modelSchema.getFields().get(MapperConstants.MODEL_ROOT);
            buildTypes(element, modelField, schema);
            Message message = definitions.newMessage(name);
            message.newPart(name, element);
        });
        definitions.addSchema(schema);


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
        Binding binding = new Binding();
//        Binding binding = definitions.newBinding(SoapConstants.BINDING);
        binding.setPortType(pt);
        soapApiInfos.forEach(apiInfo -> {
            BindingOperation bindingOperation = binding.newBindingOperation(apiInfo.getName());
            SOAPOperation soapOperation = bindingOperation.newSOAP11Operation();
            soapOperation.setName(apiInfo.getName());
            soapOperation.setSoapAction(SoapConstants.SERVER_ADDRESS + apiInfo.getPath());
            BindingInput bindingInput = bindingOperation.newInput();
            bindingInput.setName(apiInfo.getInput().getName());
//            SOAPBody inputBody = bindingInput.newSOAP11Body();
//            inputBody.setUse("literal");
            BindingOutput bindingOutput = bindingOperation.newOutput();
            bindingOutput.setName(apiInfo.getOutput().getName());
//            SOAPBody outputBody = bindingOutput.newSOAP11Body();
//            outputBody.setUse("literal");
        });
        List<Binding> bindings = new ArrayList<>();
        bindings.add(binding);
        definitions.setLocalBindings(bindings);

        /**
         * service
         */
        Service service = definitions.newService(SoapConstants.SERVICE_NAME);
        Port port = service.newPort(SoapConstants.BINDING);
        port.setBinding(binding);
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
            buildChildrenType(name, field, schema, e);
        });
    }

    private static void buildChildrenType(String name, ModelField modelField, Schema schema, Element parent) {
        if (modelField.getType() != FieldType.OBJECT && !(modelField.getType() == FieldType.ARRAY && modelField.getSubType() == FieldType.OBJECT)) {
            parent.setType(new QName(getFieldType(modelField.getType())));
            return;
        }

        parent.setType(new QName(name));
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
            buildChildrenType(fieldName, field, schema, e);
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
