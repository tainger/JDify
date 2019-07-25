package io.terminus.dalaran.component.processor;

import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.processor.mapper.DalaranMapperConfig;
import io.terminus.dalaran.component.processor.mapper.DalaranMessageMapper;
import io.terminus.dalaran.component.processor.mapper.model.MappingType;
import io.terminus.dalaran.component.processor.mapper.model.SimpleMapping;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.JsonSchema;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/25
 */
public class MapperTest extends BasicProcessorTest {

    @Test
    public void testMapperProcessor() {
        DalaranMapperConfig config = new DalaranMapperConfig();
        Map<String, SimpleMapping> messageMapping = new HashMap<>();
        SimpleMapping field1 = new SimpleMapping();
        field1.setMappingType(MappingType.MAPPING);
        field1.setValue("root.id");
        messageMapping.put("root.userId", field1);

        SimpleMapping field2 = new SimpleMapping();
        field2.setMappingType(MappingType.DEFAULT);
        field2.setValue("momo");
        messageMapping.put("root.userName", field2);

        config.setMessageMapping(messageMapping);

        MessageModel in = buildInModel();
        MessageModel out = buildOutModel();

        config.setInModel(in);
        config.setOutModel(out);

        DalaranMessageMapper messageMapper = new DalaranMessageMapper();
        ProducerTemplate template = getProcessorTemplate(messageMapper, config);

        Assert.assertNotNull(template);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "1");
        requestBody.put("name", "xxxxxx");
        Object result = template.requestBody(requestBody);
        Assert.assertNotNull(result);
    }


    private MessageModel buildInModel() {
        MessageModel in = new MessageModel();
        in.setModelType(BodyType.JSON);
        JsonSchema inSchema = new JsonSchema();
        Map<String, ModelField> inField = new HashMap<>();
        ModelField inModelField = new ModelField();
        inModelField.setType(FieldType.OBJECT);
        Map<String, ModelField> inChild = new HashMap<>();
        ModelField childField1 = new ModelField();
        childField1.setType(FieldType.STRING);
        inChild.put("id", childField1);
        ModelField childField2 = new ModelField();
        childField2.setType(FieldType.STRING);
        inChild.put("name", childField2);
        inModelField.setFields(inChild);
        inField.put("root", inModelField);
        inSchema.setFields(inField);
        in.setModelSchema(inSchema);
        return in;
    }

    private MessageModel buildOutModel() {
        MessageModel out = new MessageModel();
        out.setModelType(BodyType.JSON);
        JsonSchema inSchema = new JsonSchema();
        Map<String, ModelField> inField = new HashMap<>();
        ModelField inModelField = new ModelField();
        inModelField.setType(FieldType.OBJECT);
        Map<String, ModelField> inChild = new HashMap<>();
        ModelField childField1 = new ModelField();
        childField1.setType(FieldType.STRING);
        inChild.put("userId", childField1);
        ModelField childField2 = new ModelField();
        childField2.setType(FieldType.STRING);
        inChild.put("userName", childField2);
        inModelField.setFields(inChild);
        inField.put("root", inModelField);
        inSchema.setFields(inField);
        out.setModelSchema(inSchema);
        return out;
    }
}
