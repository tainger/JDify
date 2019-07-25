package io.terminus.dalaran.component.processor;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.processor.mapper.DalaranMapperConfig;
import io.terminus.dalaran.component.processor.mapper.DalaranMapperProcessor;
import io.terminus.dalaran.component.processor.mapper.DalaranMessageMapper;
import io.terminus.dalaran.component.processor.mapper.model.*;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.JsonSchema;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingdi on 2019/7/18
 */
public class NewMapperTest extends BasicProcessorTest {

    @Test
    public void transfer() {
        MessageModel in = buildIn();
        MessageModel out = buildOut();

        Map<String, SimpleMapping> mappingList = new HashMap<>();

        SimpleMapping simpleMapping1 = new SimpleMapping();
        simpleMapping1.setValue("root.models.id, root.models.name");
        simpleMapping1.setMappingType(MappingType.MAPPING);
        MappingFunction mappingFunction1 = new MappingFunction();
        mappingFunction1.setType(FunctionType.STANDARD);
        mappingFunction1.setKey("StringToUpper");
        simpleMapping1.setFunction(mappingFunction1);
        mappingList.put("root.modelList.userId", simpleMapping1);

        SimpleMapping simpleMapping2 = new SimpleMapping();
        simpleMapping2.setValue("root.models.name");
        simpleMapping2.setMappingType(MappingType.MAPPING);
        mappingList.put("root.modelList.userName", simpleMapping2);

        SimpleMapping simpleMapping3 = new SimpleMapping();
        simpleMapping3.setValue("root.id");
        simpleMapping3.setMappingType(MappingType.MAPPING);
        mappingList.put("root.userId", simpleMapping3);

        SimpleMapping simpleMapping4 = new SimpleMapping();
        simpleMapping4.setValue("root.name");
        simpleMapping4.setMappingType(MappingType.MAPPING);
        mappingList.put("root.userName", simpleMapping4);

        SimpleMapping simpleMapping5 = new SimpleMapping();
        simpleMapping5.setValue("root.order");
        simpleMapping5.setMappingType(MappingType.MAPPING);
        mappingList.put("root.orders", simpleMapping5);

        DalaranMapperConfig dalaranMapperConfig = new DalaranMapperConfig();
        dalaranMapperConfig.setInModel(in);
        dalaranMapperConfig.setOutModel(out);
        dalaranMapperConfig.setMessageMapping(mappingList);

        DalaranMessageMapper mapper = new DalaranMessageMapper();
        ProducerTemplate template = getProcessorTemplate(mapper, dalaranMapperConfig);

        DalaranMappingConfig mappingConfig = mapper.transfer(mappingList, in, out);

        SimpleMappingField sourceRoot = new SimpleMappingField();
        sourceRoot.setType(FieldType.OBJECT);

        SimpleMappingField destinationRoot = new SimpleMappingField();
        destinationRoot.setType(FieldType.OBJECT);

        Object source = JSON.parseObject("{\"models\":[{\"id\":1,\"name\":\"2a\"},{\"id\":2,\"name\":\"2b\"}],\"singleModel\":{\"id\":3,\"name\":\"ccccc\"},\"name\":\"momo\",\"id\":1,\"order\":[\"2aaaaa\",\"2bbbbb\",\"2nnnnnn\"]}", Object.class);

        Object result = template.requestBody(source);

        System.out.println(JSON.toJSONString(mappingConfig));

        DalaranMapperProcessor mapperProcessor = new DalaranMapperProcessor(mappingConfig, null);

        Object destination = mapperProcessor.convert(mappingConfig, source);

        System.out.println(destination);
    }

    private MessageModel buildIn() {
        MessageModel model = new MessageModel();

        ModelField modelField = JSON.parseObject("{\"fields\":{\"models\":{\"fields\":{\"name\":{\"nullable\":false,\"type\":\"STRING\"},\"id\":{\"nullable\":false,\"type\":\"INTEGER\"}},\"nullable\":false,\"subType\":\"OBJECT\",\"type\":\"ARRAY\"},\"singleModel\":{\"fields\":{\"name\":{\"nullable\":false,\"type\":\"STRING\"},\"id\":{\"nullable\":false,\"type\":\"INTEGER\"}},\"nullable\":false,\"type\":\"OBJECT\"},\"name\":{\"nullable\":false,\"type\":\"STRING\"},\"id\":{\"nullable\":false,\"type\":\"INTEGER\"},\"order\":{\"fields\":{},\"nullable\":false,\"subType\":\"STRING\",\"type\":\"ARRAY\"}},\"nullable\":false,\"type\":\"OBJECT\"}", ModelField.class);

        Map<String, ModelField> field = new HashMap<>();
        field.put("root", modelField);

        JsonSchema schema = new JsonSchema();
        schema.setFields(field);

        model.setModelSchema(schema);

        return model;
    }

    private MessageModel buildOut() {
        MessageModel model = new MessageModel();

        ModelField modelField = JSON.parseObject("{\"fields\":{\"modelList\":{\"fields\":{\"userName\":{\"nullable\":false,\"type\":\"STRING\"},\"userId\":{\"nullable\":false,\"type\":\"INTEGER\"}},\"nullable\":false,\"subType\":\"OBJECT\",\"type\":\"ARRAY\"},\"singleModel\":{\"fields\":{\"name\":{\"nullable\":false,\"type\":\"STRING\"},\"id\":{\"nullable\":false,\"type\":\"INTEGER\"}},\"nullable\":false,\"type\":\"OBJECT\"},\"orders\":{\"fields\":{},\"nullable\":false,\"subType\":\"STRING\",\"type\":\"ARRAY\"},\"userName\":{\"nullable\":false,\"type\":\"STRING\"},\"userId\":{\"nullable\":false,\"type\":\"INTEGER\"}},\"nullable\":false,\"type\":\"OBJECT\"}", ModelField.class);

        Map<String, ModelField> field = new HashMap<>();
        field.put("root", modelField);

        JsonSchema schema = new JsonSchema();
        schema.setFields(field);

        model.setModelSchema(schema);

        return model;
    }
}
