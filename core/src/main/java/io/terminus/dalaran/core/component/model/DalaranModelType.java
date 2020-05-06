package io.terminus.dalaran.core.component.model;

import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.schema.DataTemplate;
import org.apache.camel.model.ProcessorDefinition;

import java.util.Map;

public interface DalaranModelType<T, Schema extends DalaranModelSchema> {
    // TODO 其实可以搞到 DataFormat 接口的实现
    void fromObject(ProcessorDefinition route, Schema schema);

    void toObject(ProcessorDefinition route, Schema schema);

    String buildTemplateData(Map fields);

    Schema buildSchemaFromTemplateData(String dataStr);

    Schema importTemplateData(DataTemplate dataTemplate, String schema);

    Schema importDalaranSchema(Schema schema);
}
