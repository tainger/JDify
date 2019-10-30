package io.terminus.dalaran.test.processor;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.connector.SoapClientConnector;
import io.terminus.dalaran.component.processor.mapper.DalaranMapperConfig;
import io.terminus.dalaran.component.processor.soap.SoapClientConfig;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.HttpProtocol;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.component.ProcessorModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.model.schema.*;
import io.terminus.dalaran.test.TestApplication;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class ModelConvert {

    @Autowired
    private DalaranContext dalaranContext;

    private static String WSDL = "https://svn.apache.org/repos/asf/airavata/sandbox/xbaya-web/test/Calculator.wsdl";

    /**
     * object -> json
     */
    @Test
    public void basicConvert() {
        MessageModel jsonModel = buildModel("JSON");
        MessageModel objectModel = buildModel(DalaranConstants.OBJECT_MODEL_TYPE);

        BasicFlow basicFlow = new BasicFlow();
        basicFlow.setInModel(objectModel);
        basicFlow.setOutModel(jsonModel);
        basicFlow.setId(10086L);
        basicFlow.setStatus(FlowStatus.Available);
        List<ProcessorModel> pipeline = new ArrayList<>();

        ProcessorModel<DalaranMapperConfig> processor1 = new ProcessorModel<>();
        processor1.setId("2L");
        processor1.setInModel(objectModel);
        processor1.setOutModel(jsonModel);
        processor1.setConfig(new DalaranMapperConfig());
        processor1.setType("mapper-convert");
        pipeline.add(processor1);
        basicFlow.setPipeline(pipeline);
        dalaranContext.addTestFlow(basicFlow);

        String source = "{\"n1\":\"1\", \"n2\":\"2\"}";
        String rst = dalaranContext.testFlow(10086L, source);
        Assert.assertNotNull(rst);
    }

    /**
     * json -> xml
     */
    @Test
    public void xmlConvert() {
        MessageModel jsonModel = buildModel("JSON");
        MessageModel xmlModel = buildModel("XML");

        BasicFlow basicFlow = new BasicFlow();
        basicFlow.setInModel(jsonModel);
        basicFlow.setOutModel(xmlModel);
        basicFlow.setId(10086L);
        basicFlow.setStatus(FlowStatus.Available);
        List<ProcessorModel> pipeline = new ArrayList<>();

        ProcessorModel<DalaranMapperConfig> processor1 = new ProcessorModel<>();
        processor1.setId("2L");
        processor1.setInModel(jsonModel);
        processor1.setOutModel(xmlModel);
        processor1.setConfig(new DalaranMapperConfig());
        processor1.setType("mapper-convert");
        pipeline.add(processor1);
        basicFlow.setPipeline(pipeline);
        dalaranContext.addTestFlow(basicFlow);

        String source = "{\"n1\":\"1\", \"n2\":\"2\"}";
        String rst = dalaranContext.testFlow(10086L, source);
        Assert.assertNotNull(rst);
    }

    /**
     * wsdl link: https://svn.apache.org/repos/asf/airavata/sandbox/xbaya-web/test/Calculator.wsdl
     */
    @Test
    public void soapConvert() {
        MessageModel<JsonSchema> inModel = new MessageModel<>();
        JsonSchema schema = new JsonSchema();
        schema.setFields(buildRequest());
        inModel.setModelSchema(schema);
        inModel.setModelType("JSON");

        MessageModel<JsonSchema> outModel = new MessageModel<>();
        JsonSchema outModelSchema = new JsonSchema();
        outModelSchema.setFields(buildResponse());
        outModel.setModelSchema(outModelSchema);
        outModel.setModelType("JSON");

        BasicFlow basicFlow = new BasicFlow();
        basicFlow.setId(10088L);
        basicFlow.setStatus(FlowStatus.Available);
        basicFlow.setInModel(inModel);
        basicFlow.setOutModel(outModel);

        List<ProcessorModel> pipeline = new ArrayList<>();

        MessageModel<SoapSchema> request = buildSoapModel();
        request.getModelSchema().setFields(buildRequest());

        MessageModel<SoapSchema> response = buildSoapModel();
        response.getModelSchema().setFields(buildResponse());

        ProcessorModel<DalaranMapperConfig> processor2 = new ProcessorModel<>();
        processor2.setType("mapper-convert");
        processor2.setId("1L");
        processor2.setInModel(inModel);
        processor2.setOutModel(request);
        processor2.setConfig(new DalaranMapperConfig());
        pipeline.add(processor2);

        ProcessorModel<SoapClientConfig> processor1 = new ProcessorModel<>();
        processor1.setId("2L");
        processor1.setInModel(request);
        processor1.setOutModel(response);

        SoapClientConfig clientConfig = new SoapClientConfig();
        clientConfig.setMethod(HttpMethod.POST);
        clientConfig.setInModel(request);
        clientConfig.setOutModel(response);
        clientConfig.setPath("/services/Calculator.CalculatorHttpSoap11Endpoint/");
        SoapClientConnector connector = new SoapClientConnector();
        connector.setHost("156.56.179.164");
        connector.setPort(9763);
        connector.setProtocol(HttpProtocol.HTTP);
        clientConfig.setConnector(connector);
        processor1.setConfig(clientConfig);
        processor1.setType("soap-client");
        pipeline.add(processor1);
        basicFlow.setPipeline(pipeline);
        dalaranContext.addTestFlow(basicFlow);

        /**
         * request: {"n1":"1", "n2":"2"}
         * response: {"return":""}
         */
        String source = "{\"test\":{\"n1\":\"1\", \"n2\":\"2\"}}";
        String rst = dalaranContext.testFlow(10088L, source);
        Assert.assertNotNull(rst);
    }

    private MessageModel buildModel(String type) {
        ModelField modelField = JSON.parseObject("{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"nullable\":false,\"description\":\"根节点\",\"fields\":{\"user\":{\"type\":\"OBJECT\",\"subType\":null,\"nullable\":true,\"description\":\"结算单号\",\"fields\":{\"address\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"公司名称\",\"fields\":null},\"phone\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"处理状态\",\"fields\":null},\"name\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":\"单据状态\",\"fields\":null},\"wechat\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":\"结算单行项目\",\"fields\":null},\"id\":{\"type\":\"LONG\",\"subType\":null,\"nullable\":true,\"description\":\"结算单类型\",\"fields\":null}}},\"order\":{\"type\":\"OBJECT\",\"subType\":null,\"nullable\":false,\"description\":\"结算单行号\",\"fields\":{\"address\":{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"nullable\":true,\"description\":\"操作码\",\"fields\":{\"addr2\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":null,\"fields\":null},\"addr1\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"删除记录\",\"fields\":null},\"list\":{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"fields\":{\"itemA\":{\"type\":\"STRING\",\"subType\":null,\"fields\":null},\"itemB\":{\"type\":\"STRING\",\"subType\":null,\"fields\":null}}}}},\"id\":{\"type\":\"INTEGER\",\"subType\":null,\"nullable\":false,\"description\":\"删除标记\",\"fields\":null},\"time\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证年份\",\"fields\":null},\"detail\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证\",\"fields\":null},\"user\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证行号\",\"fields\":null}}}}}", ModelField.class);
        return build(modelField, type);
    }

    private MessageModel build(ModelField modelField, String type) {
        MessageModel model = new MessageModel();
        Map<String, ModelField> field = new HashMap<>();
        field.put("root", modelField);
        DalaranModelSchema schema;
        switch (type) {
            case "XML":
                schema = new XMLSchema();
                ((XMLSchema) schema).setRoot("test");
                break;
            case "SOAP":
                schema = new SoapSchema();
                break;
            case DalaranConstants.OBJECT_MODEL_TYPE:
                schema = new ObjectSchema();
                break;
            default:
                schema = new JsonSchema();
        }
        schema.setFields(field);
        model.setModelSchema(schema);
        model.setModelType(type);
        return model;
    }

    private MessageModel<SoapSchema> buildSoapModel() {
        MessageModel<SoapSchema> model = new MessageModel<>();
        model.setModelType("SOAP");

        String wsdlDoc = getWsdlDoc("https://svn.apache.org/repos/asf/airavata/sandbox/xbaya-web/test/Calculator.wsdl");
        SoapSchema schema = new SoapSchema();

        SoapSchemaOperation operation = new SoapSchemaOperation();
        schema.setOperationConfig(operation);
        model.setModelSchema(schema);
        return model;
    }

    private Map<String, ModelField> buildRequest() {
        Map<String, ModelField> request = new HashMap<>();
        request.put("root", JSON.parseObject("{\"type\":\"OBJECT\", \"fields\":{\"n1\":{\"type\":\"STRING\"}, \"n2\":{\"type\":\"STRING\"}}}", ModelField.class));
        return request;
    }

    private Map<String, ModelField> buildResponse() {
        Map<String, ModelField> response = new HashMap<>();
        response.put("root", JSON.parseObject("{\"type\":\"OBJECT\", \"fields\":{\"return\":{\"type\":\"STRING\"}}}", ModelField.class));
        return response;
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
