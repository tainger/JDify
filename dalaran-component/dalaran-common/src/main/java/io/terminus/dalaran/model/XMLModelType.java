package io.terminus.dalaran.model;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.schema.XMLSchema;
import io.terminus.dalaran.model.utils.ModelUtils;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@ModelType(value = "XML", modelSchema = XMLSchema.class)
public class XMLModelType implements DalaranModelType<String, XMLSchema> {

    @Override
    public void fromObject(ProcessorDefinition route, XMLSchema schema) {
        route.marshal().xmljson(buildOptions(schema));
        route.unmarshal().json(JsonLibrary.Fastjson);
    }

    @Override
    public void toObject(ProcessorDefinition route, XMLSchema schema) {
        route.marshal().json(JsonLibrary.Fastjson);
        route.unmarshal(buildJsonFormat(schema));
    }

    @Override
    public String buildTemplateData(XMLSchema schema) {
        Object body = ModelUtils.buildBody(schema);
        if (body != null) {
            return JSON.toJSONString(body);
        }
        return null;
    }

    @Override
    public XMLSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }

    @Override
    public XMLSchema importTemplateData(DataTemplate dataTemplate, String originSchema) {
        Object body = JSON.parse(dataTemplate.getDataTemplate());
        Map<String, ModelField> root = ModelUtils.parseDataTemplate(body);
        XMLSchema schema;
        if (StringUtils.isNotBlank(originSchema)) {
            schema = JSON.parseObject(originSchema, XMLSchema.class);
        } else {
            schema = new XMLSchema();
        }
        schema.setFields(root);
        return schema;
    }

    // TODO 处理 XML 特殊逻辑, 比如 attr, 应该可以抽象
    private Map<String, String> buildOptions(XMLSchema schema) {
        Map<String, String> options = new HashMap<>();
        options.put("rootName", schema.getRoot());
        return options;
    }

    private XmlJsonDataFormat buildJsonFormat(XMLSchema schema) {
        XmlJsonDataFormat dataFormat = new XmlJsonDataFormat();
        dataFormat.setRootName(schema.getRoot());
        return dataFormat;
    }

    @Override
    public XMLSchema importDalaranSchema(XMLSchema schema) {
        return null;
    }
}
