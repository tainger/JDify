package io.terminus.dalaran.model.converter.soap.processor;

import com.predic8.wsdl.Definitions;
import com.predic8.wstool.creator.RequestCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import groovy.xml.MarkupBuilder;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.converter.soap.model.SoapSchemaOperation;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.http.entity.StringEntity;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/6
 */
public class ObjectToSoapProcessor implements Processor, Traceable {

    private final Map<String, ModelField> modelFields;

    private final SoapSchemaOperation soapOperationConfig;

    private final Definitions definitions;

    private static final String XPATH = "xpath:";

    public ObjectToSoapProcessor(Map<String, ModelField> modelFields, SoapSchemaOperation soapOperationConfig, Definitions definitions) {
        this.modelFields = modelFields;
        this.soapOperationConfig = soapOperationConfig;
        this.definitions = definitions;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        Object requestParams = buildRequestParams(modelFields, body, XPATH + "/" + soapOperationConfig.getInput());

        StringWriter stringWriter = new StringWriter();
        RequestCreator requestCreator = new RequestCreator();
        MarkupBuilder markupBuilder = new MarkupBuilder(stringWriter);
        SOARequestCreator creator = new SOARequestCreator(definitions, requestCreator, markupBuilder);
        creator.setFormParams(requestParams);
        creator.createRequest(soapOperationConfig.getPortType(), soapOperationConfig.getName(), soapOperationConfig.getBinding());
        StringEntity stringEntity = new StringEntity(stringWriter.toString());
        stringEntity.setContentType("text/xml");
        exchange.getOut().setBody(stringEntity);
    }

    private Map<String, Object> buildRequestParams(Map<String, ModelField> modelFields, Object body, String rootPath) {
        ModelField root = modelFields.get(DalaranConstants.MODEL_ROOT);
        Map<String, ModelField> rootField = root.getFields();
        Map<String, Object> formParams = new HashMap<>();
        if (root.getType() == FieldType.ARRAY) {
            List list = (List) body;
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
        parentField.forEach((name, field) -> {
            Object subBody = ((Map) body).get(name);
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
                    List data = (List) subBody;
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

    @Override
    public String getTraceLabel() {
        return "SoapConvert: ObjectToSoap";
    }
}
