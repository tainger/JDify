package io.terminus.dalaran.model;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.schema.ObjectSchema;
import io.terminus.dalaran.model.utils.ModelUtils;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@ModelType(value = DalaranConstants.OBJECT_MODEL_TYPE, modelSchema = ObjectSchema.class)
public class ObjectModelType implements DalaranModelType<Object, ObjectSchema> {
    @Override
    public void fromObject(ProcessorDefinition route, ObjectSchema schema) {

    }

    @Override
    public void toObject(ProcessorDefinition route, ObjectSchema schema) {

    }

    @Override
    public String buildTemplateData(Map fields) {
        ObjectSchema schema = new ObjectSchema();
        schema.setFields(fields);
        Object body = ModelUtils.buildBody(schema);
        if (body != null) {
            return JSON.toJSONString(body);
        }
        return null;
    }

    @Override
    public ObjectSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }

    @Override
    public ObjectSchema importTemplateData(DataTemplate dataTemplate, String originSchema) {
        Object body = JSON.parse(dataTemplate.getDataTemplate());
        Map<String, ModelField> root = ModelUtils.parseDataTemplate(body);
        ObjectSchema schema;
        if (StringUtils.isNotBlank(originSchema)) {
            schema = JSON.parseObject(originSchema, ObjectSchema.class);
        } else {
            schema = new ObjectSchema();
        }
        schema.setFields(root);
        return schema;
    }

    @Override
    public ObjectSchema importDalaranSchema(ObjectSchema schema) {
        return null;
    }
}
