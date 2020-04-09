package io.terminus.dalaran.model.edi;


import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.schema.EDISchema;
import org.apache.camel.model.ProcessorDefinition;

import java.util.Map;

@ModelType(value = "EDI", modelSchema = EDISchema.class)
public class EDIModelType implements DalaranModelType<String, EDISchema> {

    @Override
    public void fromObject(ProcessorDefinition route, EDISchema schema) {

    }

    @Override
    public void toObject(ProcessorDefinition route, EDISchema schema) {

    }

    @Override
    public String buildTemplateData(Map fields) {
        return null;
    }

    @Override
    public EDISchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }

    @Override
    public EDISchema importTemplateData(DataTemplate dataTemplate, String originSchema) {
        return null;
    }

    @Override
    public EDISchema importDalaranSchema(EDISchema schema) {
        return null;
    }
}
