package io.terminus.dalaran.component.processor.soap;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.predic8.wsdl.Definitions;
import com.predic8.wstool.creator.RequestCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import groovy.xml.MarkupBuilder;
import io.terminus.dalaran.FieldType;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.JsonSchema;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/5/23
 */
public class DalaranSoapProcessor implements Processor {

    private final DalaranSoapConfig soapOperationConfig;

    private final Definitions definitions;

    private static final String XPATH = "xpath:";

    public DalaranSoapProcessor(DalaranSoapConfig soapOperationConfig, Definitions definitions) {
        this.soapOperationConfig = soapOperationConfig;
        this.definitions = definitions;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
//        String str = "{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"nullable\":false,\"description\":\"根节点\",\"fields\":{\"user\":{\"type\":\"OBJECT\",\"subType\":null,\"nullable\":true,\"description\":\"结算单号\",\"fields\":{\"address\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"公司名称\",\"fields\":null},\"phone\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"处理状态\",\"fields\":null},\"wechat\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":\"结算单行项目\",\"fields\":null},\"userName\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":\"单据状态\",\"fields\":null},\"userId\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"结算单类型\",\"fields\":null}}},\"order\":{\"type\":\"OBJECT\",\"subType\":null,\"nullable\":false,\"description\":\"结算单行号\",\"fields\":{\"orderTime\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证年份\",\"fields\":null},\"address\":{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"nullable\":true,\"description\":\"操作码\",\"fields\":{\"addr2\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":null,\"fields\":null},\"addr1\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"删除记录\",\"fields\":null},\"list\":{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"fields\":{\"item1\":{\"type\":\"STRING\",\"subType\":null,\"fields\":null},\"item2\":{\"type\":\"STRING\",\"subType\":null,\"fields\":null}}}}},\"orderId\":{\"type\":\"INTEGER\",\"subType\":null,\"nullable\":false,\"description\":\"删除标记\",\"fields\":null},\"orderDetail\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证\",\"fields\":null},\"user\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证行号\",\"fields\":null}}}}}";
//        ModelField field = JSON.parseObject(str, ModelField.class);
//        Map m = Collections.singletonMap("root", field);
//        String d = "[{\"user\":{\"userId\":2, \"userName\":\"momo\", \"phone\":\"10086\", \"address\":\"mmmmmm\", \"wechat\":\"9999\"}, \"order\":{\"orderId\":\"11001\", \"orderTime\":\"00:00\", \"orderDetail\":\"asdfghjkl\", \"user\":\"momo\", \"address\":[{\"addr1\":\"mmmm\", \"addr2\":\"llllll\", \"list\":[{\"item1\":\"11111\", \"item2\":\"2222222\"}]}, {\"addr1\":\"pppppp\"}]}}]";

//        WSDLParser parser = new WSDLParser();
//
//        Definitions definitions = parser.parse("http://www.learnwebservices.com/services/hello?WSDL");
//
//        List<Message> messages = definitions.getMessages();
//
//        SoapService soapService = new SoapService();
//        WSDLImportConfig importConfig = new WSDLImportConfig();
//        importConfig.setWsdlUrl(soapOperationConfig.getWsdl());
//        SoapServiceConfig serviceConfig = soapService.importConfig(importConfig);
//
//        System.out.println(JSON.toJSONString(serviceConfig));




        StringWriter stringWriter = new StringWriter();
        String body = exchange.getIn().getBody(String.class);

        MessageModel<JsonSchema> model = soapOperationConfig.getInModel();
        Map formParams = buildRequestParams(model.getModelSchema().getFields(), body, XPATH + "/" + soapOperationConfig.getInput());

        RequestCreator requestCreator = new RequestCreator();
        MarkupBuilder markupBuilder = new MarkupBuilder(stringWriter);
        SOARequestCreator creator = new SOARequestCreator(definitions, requestCreator, markupBuilder);

        creator.setFormParams(formParams);
        creator.createRequest(soapOperationConfig.getPortType(), soapOperationConfig.getName(), soapOperationConfig.getBinding());

        CloseableHttpClient client = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost(definitions.getBaseDir().toString());
        post.addHeader("Content-Type", "text/xml");
        post.setEntity(new StringEntity(stringWriter.toString()));

        CloseableHttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();

        Object ob = new Object();
        try {
            ob = formatResponse(entity, soapOperationConfig.getOutPut());
        } catch (Exception e) {
            e.printStackTrace();
        }

        exchange.getOut().setBody(ob);
    }

    private Map<String, Object> buildRequestParams(Map<String, ModelField> modelFields, String body, String rootPath) {
        ModelField root = modelFields.get(MapperConstants.MODEL_ROOT);
        Map<String, ModelField> rootField = root.getFields();
        Map<String, Object> formParams = new HashMap<>();
        if (root.getType() == FieldType.ARRAY) {
            List list = JSON.parseObject(body, List.class);
            for (int i = 0; i < list.size(); i++) {
                Object data = list.get(i);
                buildPaths(rootField, data, i, rootPath, formParams);
            }
        } else {
            buildPaths(rootField, body, -1, rootPath, formParams);
        }
        return formParams;
    }

    private void buildPaths(Map<String, ModelField> parentField, Object body, int index, String parentPath, Map<String, Object> formParams) {
        Map bodyMap = JSON.parseObject(body.toString(), Map.class);
        parentField.forEach((name, field) -> {
            Object subBody = bodyMap.get(name);
            String path;
            if (index == -1) {
                path = parentPath + "/" + name;
            } else {
                path = parentPath + "[" + index + "]" + "/" + name;
            }

            if (subBody != null) {
                Map<String, ModelField> child = field.getFields();
                FieldType type = field.getType();
                FieldType subType = field.getSubType();
                if (type == FieldType.ARRAY) {
                    List data = JSON.parseObject(JSON.toJSONString(subBody), List.class);
                    if (subType == FieldType.OBJECT) {
                        for (int i = 0; i < data.size(); i++) {
                            Object ob = data.get(i);
                            buildPaths(child, ob, i, path, formParams);
                        }
                    } else {
                        for (int i = 0; i < data.size(); i++) {
                            Object ob = data.get(i);
                            String subPath = path + "[" + i + "]";
                            formParams.put(subPath, ob);
                        }
                    }
                } else if (type == FieldType.OBJECT) {
                    buildPaths(child, subBody, -1, path, formParams);
                } else {
                    formParams.put(path, subBody);
                }
            } else {
                formParams.put(path, null);
            }
        });
    }

    private Object formatResponse(HttpEntity entity, String input) throws Exception {
        String body = EntityUtils.toString(entity);
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode jsonNode = xmlMapper.readTree(body.getBytes());
        ObjectMapper objectMapper = new ObjectMapper();
        String value = objectMapper.writeValueAsString(jsonNode);
        Map map = JSON.parseObject(value, Map.class);
        Map inputBody = (Map) map.get("Body");
        return inputBody.get(input);
    }
}
