package io.terminus.dalaran.component.convert;

import io.terminus.dalaran.component.BasicConvertTest;
import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.processor.mapper.DalaranMapperConfig;
import io.terminus.dalaran.component.processor.mapper.DalaranMessageMapper;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.XMLSchema;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonConvertTest extends BasicConvertTest {

    @Test
    public void convert() {
        List<TestProcessor> processors = new ArrayList<>();
        MessageModel jsonModel = buildJsonModel();
        MessageModel xmlModel = buildXMLModel();

        TestProcessor firstProcessor = new TestProcessor();
        DalaranMapperConfig firstMapperConfig = new DalaranMapperConfig();
        firstMapperConfig.setInModel(jsonModel);
        firstMapperConfig.setOutModel(jsonModel);
        firstMapperConfig.setMessageMapping(new HashMap<>());
        DalaranMessageMapper mapper = new DalaranMessageMapper();
        firstProcessor.setProcessor(mapper);
        firstProcessor.setConfig(firstMapperConfig);
        processors.add(firstProcessor);

        TestProcessor secProcessor = new TestProcessor();
        DalaranMapperConfig secMapperConfig = new DalaranMapperConfig();
        secMapperConfig.setInModel(xmlModel);
        secMapperConfig.setOutModel(xmlModel);
        secMapperConfig.setMessageMapping(new HashMap<>());
        DalaranMessageMapper secMapper = new DalaranMessageMapper();
        secProcessor.setProcessor(secMapper);
        secProcessor.setConfig(secMapperConfig);
        processors.add(secProcessor);

        ProducerTemplate template = getConvertTemplate(processors);

        String source = "{\"test\":{\"n1\":\"1\", \"n2\":\"2\"}}";
        Object rst = template.requestBody(source);
        Assert.assertNotNull(rst);
    }

    private MessageModel<JsonSchema> buildJsonModel() {
        MessageModel<JsonSchema> model = new MessageModel<>();
        model.setModelType(BodyType.JSON);
        return model;
    }

    private MessageModel<XMLSchema> buildXMLModel() {
        MessageModel<XMLSchema> model = new MessageModel<>();
        model.setModelType(BodyType.XML);
        XMLSchema schema = new XMLSchema();
        schema.setRoot("test");
        model.setModelSchema(schema);
        return model;
    }
}
