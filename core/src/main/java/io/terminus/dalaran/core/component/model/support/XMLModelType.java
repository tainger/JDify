package io.terminus.dalaran.core.component.model.support;

import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.XMLSchema;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;

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

        return null;
    }

    @Override
    public XMLSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
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
}
