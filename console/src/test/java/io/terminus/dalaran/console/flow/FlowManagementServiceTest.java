package io.terminus.dalaran.console.flow;

import io.terminus.dalaran.console.service.FlowManagementService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by jingdi on 2019/4/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
public class FlowManagementServiceTest {

    @Autowired
    private FlowManagementService flowManagementService;

    @Test
    public void create() {
//        BasicFlowDTO flow = new BasicFlowDTO();
//        flow.setName("dalaran-flow");
//        flow.setModuleId(1L);
//
//        Set<ProcessorDTO> processors = new HashSet<>();
//        ProcessorDTO processor1 = new ProcessorDTO();
//        processor1.setId(1L);
//        processor1.setName("dalaran-processor-1");
//        processor1.setModuleId(1L);
//        processor1.setOutModel(6L);
//        processor1.setInModel(7L);
//        processor1.setDescription("test");
//        processor1.setType("http-client");
//
//        /**
//         * {"protocol":"HTTP", "host":"localhost", "port":"8080", "path":"/test", "method":"GET"}
//         */
//        Map<String, Object> processorConfig = JSON.parseObject("{\"protocol\":\"HTTP\", \"host\":\"localhost\", \"port\":\"8080\", \"path\":\"/test\", \"method\":\"GET\"}", Map.class);
//        processor1.setConfig(processorConfig);
//
//        processors.add(processor1);
//
//        flow.setProcessors(processors);
//        flow.setDescription("test");
//
//        ModelDTO modelModel = new ModelDTO();
//        modelModel.setId(7L);
//        modelModel.setName("dalaran-model");
//        modelModel.setDescription("test");
//        modelModel.setModuleId(1L);
//        modelModel.setModelType(BodyType.JSON);
//
//
//        Map<String, Object> schema = JSON.parseObject("{\"id\":{\"type\":\"int\",\"description\":\"\",\"nullable\":\"false\"},\"name\":{\"type\":\"object\",\"description\":\"\",\"nullable\":\"false\",\"fields\":{\"firstName\":{\"type\":\"string\",\"description\":\"\",\"nullable\":\"false\"},\"secondName\":{\"type\":\"string\",\"description\":\"\",\"nullable\":\"false\"}}},\"friends\":{\"type\":\"list\",\"subType\":\"object\",\"description\":\"\",\"nullable\":\"false\",\"fields\":{\"name\":{\"type\":\"string\",\"description\":\"\",\"nullable\":\"false\"},\"age\":{\"type\":\"int\",\"description\":\"\",\"nullable\":\"false\"},\"address\":{\"type\":\"string\",\"description\":\"\",\"nullable\":\"false\"}}},\"phoneNumbers\":{\"type\":\"list\",\"subType\":\"long\",\"description\":\"\",\"nullable\":\"false\"}}", Map.class);
//        modelModel.setModelSchema(schema);
//
//        flow.setInModel(modelModel);
//        flow.setOutModel(modelModel);
//
//        List<Long> processorIds = new LinkedList<>();
//        processorIds.add(1L);
//        flow.setProcessingPipeline(processorIds);
//
//        Long id = flowManagementService.createFlow(flow);

        Assert.assertTrue("", true);
    }

    public void delete() {
        flowManagementService.deleteFlow(12L);
        Assert.assertTrue("", true);
    }


}
