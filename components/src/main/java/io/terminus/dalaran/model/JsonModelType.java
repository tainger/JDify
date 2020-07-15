package io.terminus.dalaran.model;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.json.JsonMarshalPreProcessor;
import io.terminus.dalaran.model.json.JsonUnmarshalPreProcessor;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.utils.ModelUtils;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@ModelType(value = "JSON", modelSchema = JsonSchema.class)
public class JsonModelType implements DalaranModelType<String, JsonSchema> {

    @Override
    public void fromObject(ProcessorDefinition route, JsonSchema schema) {
        route.process(new JsonMarshalPreProcessor());
        route.marshal().json(JsonLibrary.Fastjson);
    }

    @Override
    public void toObject(ProcessorDefinition route, JsonSchema schema) {
        route.process(new JsonUnmarshalPreProcessor());
        route.unmarshal().json(JsonLibrary.Fastjson);
    }

    @Override
    public String buildTemplateData(Map fields) {
        JsonSchema schema = new JsonSchema();
        schema.setFields(fields);
        Object body = ModelUtils.buildBody(schema);
        if (body != null) {
            return JSON.toJSONString(body);
        }
        return null;
    }

    @Override
    public JsonSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }

    @Override
    public JsonSchema importTemplateData(DataTemplate dataTemplate, String originSchema) {
        Object body = JSON.parse(dataTemplate.getDataTemplate());
        Map<String, ModelField> root = ModelUtils.parseDataTemplate(body);
        JsonSchema schema;
        if (StringUtils.isNotBlank(originSchema)) {
            schema = JSON.parseObject(originSchema, JsonSchema.class);
        } else {
            schema = new JsonSchema();
        }
        schema.setFields(root);
        return schema;
    }

    @Override
    public JsonSchema importDalaranSchema(JsonSchema schema) {
        return null;
    }
}
