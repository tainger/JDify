package io.terminus.dalaran.model;

import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.schema.CsvModelSchema;
import org.apache.camel.model.ProcessorDefinition;

@ModelType(value = "CSV", modelSchema = CsvModelSchema.class)
public class CsvModelType implements DalaranModelType<String, CsvModelSchema> {
    @Override
    public void fromObject(ProcessorDefinition route, CsvModelSchema schema) {
        route.marshal().csv();
    }

    @Override
    public void toObject(ProcessorDefinition route, CsvModelSchema schema) {
        route.unmarshal().csv();
    }

    @Override
    public String buildTemplateData(CsvModelSchema schema) {
        return null;
    }

    @Override
    public CsvModelSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }
}
