package io.terminus.dalaran.component.processor.soap;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.predic8.wsdl.Definitions;
import com.predic8.wstool.creator.RequestCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import groovy.xml.MarkupBuilder;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import io.terminus.dalaran.component.processor.soap.jackson.DalaranObjectDeserializer;
import io.terminus.dalaran.component.processor.soap.jackson.DalaranXMLStreamReader;
import io.terminus.dalaran.core.model.FieldType;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.model.ModelField;
import io.terminus.dalaran.core.model.schema.JsonSchema;
import io.terminus.dalaran.service.soap.SoapOperationConfig;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/5/23
 */
public class DalaranSoapProcessor implements Processor {

    private final SoapOperationConfig soapOperationConfig;

    private final Definitions definitions;

    private static final String XPATH = "xpath:";

    public DalaranSoapProcessor(SoapOperationConfig soapOperationConfig, Definitions definitions) {
        this.soapOperationConfig = soapOperationConfig;
        this.definitions = definitions;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
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
        exchange.getOut().setBody(JSON.toJSON(ob));
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
        InputStream is = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        DalaranXMLStreamReader sr = new DalaranXMLStreamReader(XMLInputFactory.newFactory().createXMLStreamReader(is));
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new SimpleModule().addDeserializer(Object.class, new DalaranObjectDeserializer()));
        Map map = (Map) xmlMapper.readValue(sr, Object.class);
        Map inputBody = (Map) map.get("Body");
        return inputBody.get(input);
    }
}
