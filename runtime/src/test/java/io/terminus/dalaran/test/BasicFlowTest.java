package io.terminus.dalaran.test;

import io.terminus.dalaran.component.processor.script.DalaranScriptConfig;
import io.terminus.dalaran.component.processor.script.DalaranScriptType;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ProcessorModel;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.XMLSchema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class BasicFlowTest {

    // TODO trigger test
    // TODO load from db test
    // TODO log test

    @Autowired
    private DalaranContext dalaranContext;

    private static final String TEST_ORDER_PREFIX = "terminus-";

    @Test
    public void test() {
        // TODO 需要被抽象, 理论上测试 flow, trigger 和 test flow 都需要这部分数据
        Map<Long, ProcessorModel> processorModelMap = new HashMap<>();
        DalaranScriptConfig config = new DalaranScriptConfig();
        config.setType(DalaranScriptType.JavaScript);
        config.setScript("function execute(header, body) {return { price: body.orderPrice * 100, no: '" + TEST_ORDER_PREFIX + "' + body.orderNumber}}");

        ProcessorModel processorModel = new ProcessorModel();
        processorModel.setId("test-processor-01");
        processorModel.setConfig(config);
        processorModel.setType("script");

        processorModelMap.put(998L, processorModel);


        XMLSchema xmlSchema = new XMLSchema();
        xmlSchema.setRoot("DalaranTest");

        MessageModel xmlModel = new MessageModel();
        xmlModel.setModelType("XML");
        xmlModel.setModelSchema(xmlSchema);

        MessageModel jsonModel = new MessageModel();
        jsonModel.setModelType("JSON");
        jsonModel.setModelSchema(new JsonSchema());

//        BasicFlow flow = new BasicFlow();
//
//        flow.setProcessorMap(processorModelMap);
//        flow.setInModel(xmlModel);
//        flow.setOutModel(jsonModel);
//        flow.setId(1L);
//
//        dalaranContext.addTestFlow(flow);
//
//        Double orderPrice = 1.25;
//
//        String orderNumber = "test-terminus-order";
//
//        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
//                "<DalaranTest>\n" +
//                "\t<orderPrice>" + orderPrice + "</orderPrice>\n" +
//                "\t<orderNumber>" + orderNumber + "</orderNumber>\n" +
//                "</DalaranTest>";
//
//        InputStream input = new ByteArrayInputStream(body.getBytes());
//
//        byte[] result = (byte[]) dalaranContext.testFlow(1L, input, "TEST_RECORD_ID");
//        String json = new String(result);
//
//        Map data = gson.fromJson(json, Map.class);
//        Double price = (Double) data.get("price");
//        String no = (String) data.get("no");
//        Assert.assertEquals(price.compareTo(orderPrice * 100), 0);
//        Assert.assertEquals(no, TEST_ORDER_PREFIX + orderNumber);
    }
}
