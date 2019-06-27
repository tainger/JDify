package io.terminus.dalaran.component.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.IOUtils;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.terminus.dalaran.component.BasicServiceTest;
import io.terminus.dalaran.core.model.*;
import io.terminus.dalaran.core.model.converter.soap.model.SoapOperationConfig;
import io.terminus.dalaran.core.model.converter.soap.model.SoapSchemaOperation;
import io.terminus.dalaran.core.model.converter.soap.processor.RequestConvertProcessor;
import io.terminus.dalaran.core.model.converter.soap.processor.ResponseConvertProcessor;
import io.terminus.dalaran.core.model.schema.SoapSchema;
import io.terminus.dalaran.service.soap.SoapService;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/25
 */
public class SoapServiceTest extends BasicServiceTest {

    @Test
    public void testSoapService() {
        SoapService service = new SoapService();

        MessageModel<SoapSchema> model = buildModel();

        SoapOperationConfig operationConfig = new SoapOperationConfig();
        operationConfig.setProtocol(HttpProtocol.HTTP);
        operationConfig.setBaseUrl("127.0.0.1:8081/ws");
        operationConfig.setInModel(model);
        operationConfig.setOutModel(model);

        Map<String, List<String>> requestBody = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("China");
        list.add("Spain");
        requestBody.put("name", list);

        WSDLParser parser = new WSDLParser();
        Definitions definitions = new Definitions();
        try {
            definitions = parser.parse(model.getModelSchema().getOperationConfig().getWsdl());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestConvertProcessor requestConvertProcessor = new RequestConvertProcessor(model.getModelSchema().getFields(), model.getModelSchema().getOperationConfig(), definitions);
        ResponseConvertProcessor responseConvertProcessor = new ResponseConvertProcessor(model.getModelSchema().getOperationConfig());
        ProducerTemplate template = getProcessorTemplate(service, operationConfig, requestConvertProcessor, responseConvertProcessor);
        Assert.assertNotNull(template);

        Object result = template.requestBody(requestBody);
        Assert.assertNotNull(result);
    }

    private MessageModel<SoapSchema> buildModel() {
        MessageModel<SoapSchema> model = new MessageModel<>();

        SoapSchemaOperation schemaOperation = new SoapSchemaOperation();
        schemaOperation.setProtocol(HttpProtocol.HTTP);
        schemaOperation.setBaseUrl("127.0.0.1:8081/ws");
        schemaOperation.setPortType("getCountryRequest");
        schemaOperation.setName("getCountry");
        schemaOperation.setInput("getCountryRequest");
        schemaOperation.setOutPut("getCountryResponse");
        schemaOperation.setBinding("CountriesPortSoap11");
        schemaOperation.setWsdl("http://127.0.0.1:8081/ws/countries.wsdl");

        SoapSchema schema = new SoapSchema();
        schema.setOperationConfig(schemaOperation);
        schema.setWsdlDoc("http://127.0.0.1:8081/ws/countries.wsdl");
        schema.setFields(buildFields());

        model.setModelType(BodyType.SOAP);
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
