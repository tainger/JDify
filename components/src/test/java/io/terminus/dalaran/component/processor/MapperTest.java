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
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jingdi on 2019/7/18
 */
public class MapperTest extends BasicProcessorTest {

    @Test
    public void complexArrayMapper() {
        MessageModel in = buildComplexIn();
        MessageModel out = buildComplexOut();

        LinkedHashMap<String, SimpleMapping> mappingList = new LinkedHashMap<>();

        /**
         * 第一层数组字段映射
         */
        SimpleMapping<String> simpleMapping2 = new SimpleMapping<>();
        simpleMapping2.setMappingType(MappingType.MAPPING);
        simpleMapping2.setValue("root.user.name");
        mappingList.put("root.user.userName", simpleMapping2);

        /**
         * 第一层数组字段映射 + 字段类型转换：long -> float
         */
        SimpleMapping<String> simpleMapping4 = new SimpleMapping<>();
        simpleMapping4.setMappingType(MappingType.MAPPING);
        simpleMapping4.setValue("root.user.id");
        mappingList.put("root.user.userId", simpleMapping4);

        /**
         * 第一层数组字段映射 + 字段类型转换：string -> date
         */
        SimpleMapping<String> simpleMapping5 = new SimpleMapping<>();
        simpleMapping5.setMappingType(MappingType.MAPPING);
        simpleMapping5.setValue("root.order.time");
        mappingList.put("root.order.orderTime", simpleMapping5);

        /**
         * 第一层数组字段映射 + 标准函数：toUpper
         */
        SimpleMapping<MappingFunction> simpleMapping1 = new SimpleMapping<>();
        simpleMapping1.setMappingType(MappingType.FUNCTION);
        MappingFunction mappingFunction1 = new MappingFunction();
        mappingFunction1.setId("StringToUpper");
        mappingFunction1.setType(FunctionType.STATIC);
        FunctionParam functionParam1 = new FunctionParam();
        functionParam1.setValue("root.user.address");
        functionParam1.setType(ParamType.DYNAMIC);
        Map<String, FunctionParam> paramMap1 = new HashMap<>();
        paramMap1.put("str", functionParam1);
        simpleMapping1.setValue(mappingFunction1);
        mappingList.put("root.user.address", simpleMapping1);

        /**
         * 第二层数组字段映射
         */
        SimpleMapping<String> simpleMapping3 = new SimpleMapping<>();
        simpleMapping3.setMappingType(MappingType.MAPPING);
        simpleMapping3.setValue("root.order.address.list.itemA");
        mappingList.put("root.order.address.list.item1", simpleMapping3);

        DalaranMapperConfig dalaranMapperConfig = new DalaranMapperConfig();
        dalaranMapperConfig.setInModel(in);
        dalaranMapperConfig.setOutModel(out);
        dalaranMapperConfig.setMessageMapping(mappingList);

        DalaranMessageMapper mapper = new DalaranMessageMapper();
        ProducerTemplate template = getProcessorTemplate(mapper, dalaranMapperConfig);
        Object source = JSON.parseObject("[{\"user\":{\"id\":2, \"name\":\"momo\", \"phone\":\"10086\", \"address\":\"mmmmmm\", \"wechat\":\"9999\"}, \"order\":{\"id\":\"11001\", \"time\":\"00:00\", \"detail\":\"asdfghjkl\", \"user\":\"momo\", \"address\":[{\"addr1\":\"mmmm\", \"addr2\":\"llllll\", \"list\":[{\"itemA\":\"11111\", \"itemB\":\"2222222\"}]}, {\"addr1\":\"pppppp\"}]}}]");
        Object result = template.requestBody(source);
        Assert.assertNotNull(result);
    }

    @Test
    public void basicMapper() {
        MessageModel in = buildIn();
        MessageModel out = buildOut();

        LinkedHashMap<String, SimpleMapping> mappingList = new LinkedHashMap<>();

        SimpleMapping simpleMapping1 = new SimpleMapping();
        simpleMapping1.setValue("root.models.id, root.models.name");
        simpleMapping1.setMappingType(MappingType.MAPPING);
        MappingFunction mappingFunction1 = new MappingFunction();
        mappingFunction1.setType(FunctionType.STATIC);
        mappingFunction1.setId("StringToUpper");
//        simpleMapping1.setFunction(mappingFunction1);
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
        ModelField modelField = JSON.parseObject("{\"fields\":{\"models\":{\"fields\":{\"name\":{\"nullable\":false,\"type\":\"STRING\"},\"id\":{\"nullable\":false,\"type\":\"INTEGER\"}},\"nullable\":false,\"subType\":\"OBJECT\",\"type\":\"ARRAY\"},\"singleModel\":{\"fields\":{\"name\":{\"nullable\":false,\"type\":\"STRING\"},\"id\":{\"nullable\":false,\"type\":\"INTEGER\"}},\"nullable\":false,\"type\":\"OBJECT\"},\"name\":{\"nullable\":false,\"type\":\"STRING\"},\"id\":{\"nullable\":false,\"type\":\"INTEGER\"},\"order\":{\"fields\":{},\"nullable\":false,\"subType\":\"STRING\",\"type\":\"ARRAY\"}},\"nullable\":false,\"type\":\"OBJECT\"}", ModelField.class);
        return buildModel(modelField);
    }

    private MessageModel buildOut() {
        ModelField modelField = JSON.parseObject("{\"fields\":{\"modelList\":{\"fields\":{\"userName\":{\"nullable\":false,\"type\":\"STRING\"},\"userId\":{\"nullable\":false,\"type\":\"INTEGER\"}},\"nullable\":false,\"subType\":\"OBJECT\",\"type\":\"ARRAY\"},\"singleModel\":{\"fields\":{\"name\":{\"nullable\":false,\"type\":\"STRING\"},\"id\":{\"nullable\":false,\"type\":\"INTEGER\"}},\"nullable\":false,\"type\":\"OBJECT\"},\"orders\":{\"fields\":{},\"nullable\":false,\"subType\":\"STRING\",\"type\":\"ARRAY\"},\"userName\":{\"nullable\":false,\"type\":\"STRING\"},\"userId\":{\"nullable\":false,\"type\":\"INTEGER\"}},\"nullable\":false,\"type\":\"OBJECT\"}", ModelField.class);
        return buildModel(modelField);
    }

    private MessageModel buildComplexIn() {
        ModelField modelField = JSON.parseObject("{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"nullable\":false,\"description\":\"根节点\",\"fields\":{\"user\":{\"type\":\"OBJECT\",\"subType\":null,\"nullable\":true,\"description\":\"结算单号\",\"fields\":{\"address\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"公司名称\",\"fields\":null},\"phone\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"处理状态\",\"fields\":null},\"name\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":\"单据状态\",\"fields\":null},\"wechat\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":\"结算单行项目\",\"fields\":null},\"id\":{\"type\":\"LONG\",\"subType\":null,\"nullable\":true,\"description\":\"结算单类型\",\"fields\":null}}},\"order\":{\"type\":\"OBJECT\",\"subType\":null,\"nullable\":false,\"description\":\"结算单行号\",\"fields\":{\"address\":{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"nullable\":true,\"description\":\"操作码\",\"fields\":{\"addr2\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":null,\"fields\":null},\"addr1\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"删除记录\",\"fields\":null},\"list\":{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"fields\":{\"itemA\":{\"type\":\"STRING\",\"subType\":null,\"fields\":null},\"itemB\":{\"type\":\"STRING\",\"subType\":null,\"fields\":null}}}}},\"id\":{\"type\":\"INTEGER\",\"subType\":null,\"nullable\":false,\"description\":\"删除标记\",\"fields\":null},\"time\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证年份\",\"fields\":null},\"detail\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证\",\"fields\":null},\"user\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证行号\",\"fields\":null}}}}}", ModelField.class);
        return buildModel(modelField);
    }

    private MessageModel buildComplexOut() {
        ModelField modelField = JSON.parseObject("{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"nullable\":false,\"description\":\"根节点\",\"fields\":{\"user\":{\"type\":\"OBJECT\",\"subType\":null,\"nullable\":true,\"description\":\"结算单号\",\"fields\":{\"address\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"公司名称\",\"fields\":null},\"phone\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"处理状态\",\"fields\":null},\"wechat\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":\"结算单行项目\",\"fields\":null},\"userName\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":\"单据状态\",\"fields\":null},\"userId\":{\"type\":\"FLOAT\",\"subType\":null,\"nullable\":true,\"description\":\"结算单类型\",\"fields\":null}}},\"order\":{\"type\":\"OBJECT\",\"subType\":null,\"nullable\":false,\"description\":\"结算单行号\",\"fields\":{\"orderTime\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证年份\",\"fields\":null},\"address\":{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"nullable\":true,\"description\":\"操作码\",\"fields\":{\"addr2\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":false,\"description\":null,\"fields\":null},\"addr1\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"删除记录\",\"fields\":null},\"list\":{\"type\":\"ARRAY\",\"subType\":\"OBJECT\",\"fields\":{\"item1\":{\"type\":\"STRING\",\"subType\":null,\"fields\":null},\"item2\":{\"type\":\"STRING\",\"subType\":null,\"fields\":null}}}}},\"orderId\":{\"type\":\"INTEGER\",\"subType\":null,\"nullable\":false,\"description\":\"删除标记\",\"fields\":null},\"orderDetail\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证\",\"fields\":null},\"user\":{\"type\":\"STRING\",\"subType\":null,\"nullable\":true,\"description\":\"物料凭证行号\",\"fields\":null}}}}}", ModelField.class);
        return buildModel(modelField);
    }

    private MessageModel buildModel(ModelField modelField) {
        MessageModel model = new MessageModel();
        Map<String, ModelField> field = new HashMap<>();
        field.put("root", modelField);
        JsonSchema schema = new JsonSchema();
        schema.setFields(field);
        model.setModelSchema(schema);
        return model;
    }
}
