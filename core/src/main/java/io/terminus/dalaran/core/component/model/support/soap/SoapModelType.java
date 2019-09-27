package io.terminus.dalaran.core.component.model.support.soap;

import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.core.component.model.support.soap.processor.ObjectToSoapProcessor;
import io.terminus.dalaran.core.component.model.support.soap.processor.SoapToObjectProcessor;
import io.terminus.dalaran.model.schema.SoapSchema;
import org.apache.camel.model.ProcessorDefinition;

@ModelType(value = "SOAP", modelSchema = SoapSchema.class)
public class SoapModelType implements DalaranModelType<String, SoapSchema> {

    @Override
    public void fromObject(ProcessorDefinition route, SoapSchema schema) {
        ObjectToSoapProcessor processor = new ObjectToSoapProcessor(schema.getFields(), schema.getOperationConfig());
        route.process(processor);
    }

    @Override
    public void toObject(ProcessorDefinition route, SoapSchema schema) {
        SoapToObjectProcessor processor = new SoapToObjectProcessor(schema.getOperationConfig());
        route.process(processor);
    }

    @Override
    public String buildTemplateData(SoapSchema schema) {
        return null;
    }

    @Override
    public SoapSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }
}
