package io.terminus.dalaran.model.soap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.schema.SoapSchema;
import io.terminus.dalaran.model.schema.SoapSchemaOperation;
import io.terminus.dalaran.model.soap.jackson.DalaranObjectDeserializer;
import io.terminus.dalaran.model.soap.jackson.DalaranXMLStreamReader;
import io.terminus.dalaran.model.soap.processor.ObjectToSoapProcessor;
import io.terminus.dalaran.model.soap.processor.SoapToObjectProcessor;
import io.terminus.dalaran.model.utils.ModelUtils;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@ModelType(value = "SOAP", modelSchema = SoapSchema.class)
public class SoapModelType implements DalaranModelType<String, SoapSchema> {

    @Override
    public void fromObject(ProcessorDefinition route, SoapSchema schema) {
        ObjectToSoapProcessor processor = new ObjectToSoapProcessor(schema);
        route.process(processor);
    }

    @Override
    public void toObject(ProcessorDefinition route, SoapSchema schema) {
        Map fields = new HashMap();
        if (schema != null) {
            fields = schema.getFields().get(DalaranConstants.MODEL_ROOT).getFields();
        }
        SoapToObjectProcessor processor = new SoapToObjectProcessor(fields);
        route.process(processor);
    }

    @Override
    public String buildTemplateData(SoapSchema soapSchema) {
        Object body = ModelUtils.buildBody(soapSchema);
        ObjectToSoapProcessor processor = new ObjectToSoapProcessor(soapSchema);
        ModelField field = soapSchema.getFields().get(DalaranConstants.MODEL_ROOT);
        try {
            return processor.buildSoapBody(field, body);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("build template data error!");
        }
    }

    @Override
    public SoapSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }

    @Override
    public SoapSchema importTemplateData(DataTemplate dataTemplate, String originSchema) {
        InputStream is = new ByteArrayInputStream(dataTemplate.getDataTemplate().getBytes(StandardCharsets.UTF_8));
        try {
            DalaranXMLStreamReader sr = new DalaranXMLStreamReader(XMLInputFactory.newFactory().createXMLStreamReader(is));
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.registerModule(new SimpleModule().addDeserializer(Object.class, new DalaranObjectDeserializer()));
            Map<String, Object> map = (Map) xmlMapper.readValue(sr, Object.class);
            SoapSchema soapSchema;
            if (StringUtils.isNotBlank(originSchema)) {
                soapSchema = JSON.parseObject(originSchema, SoapSchema.class);
            } else {
                soapSchema = new SoapSchema();
            }
            Object body = map.getOrDefault("Body", null);
            if (body != null) {
                Map<String, ModelField> root = ModelUtils.parseDataTemplate(JSON.toJSON(body));
                soapSchema.setFields(root);
            }
//            SoapSchemaOperation schemaOperation = new SoapSchemaOperation();
//            map.forEach((k, v) -> {
//                if (StringUtils.startsWithIgnoreCase(k, ComponentConstants.XMLNS) && StringUtils.endsWithIgnoreCase(k, ComponentConstants.SOAP_ENV)) {
//                    schemaOperation.setPrefix(StringUtils.substringAfter(k, ComponentConstants.XMLNS));
//                    schemaOperation.setTargetNamespace(v.toString());
//                }
//            });
//            soapSchema.setOperationConfig(schemaOperation);
            return soapSchema;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("parse data template error!");
        }
    }

    @Override
    public SoapSchema importDalaranSchema(SoapSchema schema) {
        return null;
    }
}
