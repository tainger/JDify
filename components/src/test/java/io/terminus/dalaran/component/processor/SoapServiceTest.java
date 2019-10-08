package io.terminus.dalaran.component.processor;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.component.BasicServiceTest;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.HttpProtocol;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.SoapSchema;
import io.terminus.dalaran.model.schema.SoapSchemaOperation;
import io.terminus.dalaran.model.soap.model.SoapOperationConfig;
import io.terminus.dalaran.model.soap.processor.ObjectToSoapProcessor;
import io.terminus.dalaran.model.soap.processor.SoapToObjectProcessor;
import io.terminus.dalaran.service.soap.SoapService;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/25
 */
public class SoapServiceTest extends BasicServiceTest {

    @Test
    public void SMObject2Soap() {
        SoapSchema schema1 = JSON.parseObject("{\"operationConfig\":{\"input\":\"MT_COMMON_REQ\",\"outPut\":\"MT_COMMON_RES\",\"portType\":\"SI_COMMON_S_OUT\",\"baseUrl\":\"piqas.shimaogroup.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_HYPERSMART&receiverParty=&receiverService=&interface=SI_COMMON_S_OUT&interfaceNamespace=urn%3A%3Ashimaogroup.com%3AI_HYPERSMART%3AECC\",\"protocol\":\"HTTP\",\"wsdl\":\"http://piqas.shimaogroup.com:50000/dir/wsdl?p=sa/2578ce33cd913812bbef5120fdee2c23\",\"name\":\"SI_COMMON_S_OUT\",\"binding\":\"SI_COMMON_S_OUTBinding\",\"modelRoot\":\"MT_COMMON_REQ\"},\"fields\":{\"root\":{\"nullable\":false,\"fields\":{\"MT_COMMON_REQ\":{\"type\":\"OBJECT\",\"fields\":{\"XMLDATA\":{\"nullable\":false,\"fields\":{},\"type\":\"STRING\"},\"TYPE\":{\"nullable\":false,\"fields\":{},\"type\":\"STRING\"}}}},\"type\":\"OBJECT\"}}}", SoapSchema.class);
        schema1.getOperationConfig().setTargetNamespace("urn::shimaogroup.com:I_HYPERSMART:ECC");

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> child = new HashMap<>();
        child.put("XMLDATA", "");
        child.put("TYPE", "MM_WM");
        requestBody.put("MT_COMMON_REQ", child);

        SoapOperationConfig operationConfig = new SoapOperationConfig();
        operationConfig.setProtocol(HttpProtocol.HTTP);
        operationConfig.setLocation("piqas.shimaogroup.com:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BS_HYPERSMART&receiverParty=&receiverService=&interface=SI_COMMON_S_OUT&interfaceNamespace=urn%3A%3Ashimaogroup.com%3AI_HYPERSMART%3AECC&authMethod=Basic&authUsername=HYPERS_PI&authPassword=HYPERS_PI2019");

        SoapService service = new SoapService();
        ObjectToSoapProcessor objectToSoapProcessor = new ObjectToSoapProcessor(schema1.getFields(), schema1.getOperationConfig());
        SoapToObjectProcessor soapToObjectProcessor = new SoapToObjectProcessor(schema1.getOperationConfig());
        ProducerTemplate template = getProcessorTemplate(service, operationConfig, objectToSoapProcessor, soapToObjectProcessor);

        Object result = template.requestBody(requestBody);

        Assert.assertNotNull(result);
    }

    @Test
    public void testSoapService() {
        String authorization = new BASE64Encoder().encode(("admin" + ":" + "secret").getBytes());
        SoapService service = new SoapService();

        MessageModel<SoapSchema> model = buildModel();

        SoapOperationConfig operationConfig = new SoapOperationConfig();
        operationConfig.setProtocol(HttpProtocol.HTTP);
        operationConfig.setLocation("127.0.0.1:8081/ws");
//        operationConfig.setInModel(model);
//        operationConfig.setOutModel(model);

        Map<String, List<String>> requestBody = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("China");
        list.add("Spain");
        requestBody.put("name", list);

        ObjectToSoapProcessor objectToSoapProcessor = new ObjectToSoapProcessor(model.getModelSchema().getFields(), model.getModelSchema().getOperationConfig());
        SoapToObjectProcessor soapToObjectProcessor = new SoapToObjectProcessor(model.getModelSchema().getOperationConfig());
        ProducerTemplate template = getProcessorTemplate(service, operationConfig, objectToSoapProcessor, soapToObjectProcessor);
        Assert.assertNotNull(template);

        Object result = template.requestBody(requestBody);
        Assert.assertNotNull(result);
    }

    private MessageModel<SoapSchema> buildModel() {
        MessageModel<SoapSchema> model = new MessageModel<>();

        SoapSchemaOperation schemaOperation = new SoapSchemaOperation();
        SoapSchema schema = new SoapSchema();
        schema.setOperationConfig(schemaOperation);
        schema.setFields(buildFields());

        model.setModelType("SOAP");
        model.setModelSchema(schema);

        return model;
    }

    Map<String, ModelField> buildFields() {
        Map<String, ModelField> fields = new HashMap<>();
        ModelField field = new ModelField();
        field.setType(FieldType.OBJECT);

        Map<String, ModelField> child = new HashMap<>();
        ModelField childField = new ModelField();
        childField.setType(FieldType.ARRAY);
        childField.setSubType(FieldType.STRING);
        child.put("name", childField);
        field.setFields(child);
        fields.put("root", field);

        return fields;
    }

}
