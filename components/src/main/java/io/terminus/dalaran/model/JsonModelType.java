package io.terminus.dalaran.model;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.utils.ModelUtils;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Map;

@ModelType(value = "JSON", modelSchema = JsonSchema.class)
public class JsonModelType implements DalaranModelType<String, JsonSchema> {

    @Override
    public void fromObject(ProcessorDefinition route, JsonSchema schema) {
        route.marshal().json(JsonLibrary.Fastjson);
    }

    @Override
    public void toObject(ProcessorDefinition route, JsonSchema schema) {
        route.unmarshal().json(JsonLibrary.Fastjson);
    }

    @Override
    public String buildTemplateData(JsonSchema schema) {
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
    public JsonSchema importTemplateData(DataTemplate dataTemplate) {
        Object body = JSON.parse(dataTemplate.getDataTemplate());
        Map<String, ModelField> root = ModelUtils.parseDataTemplate(body);
        JsonSchema schema = new JsonSchema();
        schema.setFields(root);
        return schema;
    }

    @Override
    public JsonSchema importDalaranSchema(JsonSchema schema) {
        return null;
    }
}
