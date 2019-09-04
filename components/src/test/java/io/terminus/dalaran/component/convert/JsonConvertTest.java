package io.terminus.dalaran.component.convert;

import com.github.drapostolos.typeparser.TypeParser;
import io.terminus.dalaran.component.BasicConvertTest;
import io.terminus.dalaran.component.processor.mapper.DalaranMapperConfig;
import io.terminus.dalaran.component.processor.mapper.DalaranMessageMapper;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.XMLSchema;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.beanutils.ConvertUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonConvertTest extends BasicConvertTest {

    @Test
    public void convert() {

        Object o1 = parse(2000000.0, FieldType.INTEGER);
        Object o2 = parse(2000000, FieldType.FLOAT);
        Object o3 = parse(2000000.0, FieldType.STRING);
        Object o4 = parse("false", FieldType.BOOLEAN);


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

    private  Object parse(Object target, FieldType destination) {
        if (target == null) {
            return null;
        }
        if (destination != null) {
            switch (destination) {
                case INTEGER:
                    return ConvertUtils.convert(target, Long.class);
                case FLOAT:
                    return ConvertUtils.convert(target, Double.class);
                case BOOLEAN:
                    return ConvertUtils.convert(target, Boolean.class);
                default:
                    return target;
            }
        }
        return target;
    }
}
