package io.terminus.dalaran.core.component.model.support;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.schema.ObjectSchema;
import org.apache.camel.model.ProcessorDefinition;

@ModelType(value = DalaranConstants.OBJECT_MODEL_TYPE, modelSchema = ObjectSchema.class)
public class ObjectModelType implements DalaranModelType<Object, ObjectSchema> {
    @Override
    public void fromObject(ProcessorDefinition route, ObjectSchema schema) {

    }

    @Override
    public void toObject(ProcessorDefinition route, ObjectSchema schema) {

    }

    @Override
    public String buildTemplateData(ObjectSchema schema) {
        return null;
    }

    @Override
    public ObjectSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }
}
