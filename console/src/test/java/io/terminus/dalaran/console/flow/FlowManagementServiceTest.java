package io.terminus.dalaran.console.flow;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.model.dto.ConnectorDTO;
import io.terminus.dalaran.console.model.dto.CopyFlow;
import io.terminus.dalaran.console.model.dto.ProcessorDTO;
import io.terminus.dalaran.console.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.repository.TriggerFlowRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.core.component.ComponentType;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.flow.TriggerFlow;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jingdi on 2019/4/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
public class FlowManagementServiceTest {

    @Autowired
    private FlowManagementService flowManagementService;

    @Autowired
    private TriggerFlowRepository triggerFlowRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private TriggerFlowRepository flowRepository;

    @Autowired
    private DalaranFlowBuilder flowBuilder;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Test
    public void create() {
        TriggerFlowDTO triggerFlow = buildTriggerFlowDTO();
        Long id = flowManagementService.createFlow(triggerFlow);
        TriggerFlowEntity entity = flowRepository.findOne(id);
        Assert.assertEquals(triggerFlow.getName(), entity.getName());
    }

    @Test
    public void save() {
        TriggerFlowEntity entity = buildEntity(buildTriggerFlowDTO());
        Long id = flowManagementService.saveFlow(entity);
        TriggerFlowEntity triggerFlow = triggerFlowRepository.findOne(id);
        Assert.assertEquals(entity.getName(), triggerFlow.getName());
    }

    @Test
    public void update() {
        TriggerFlowDTO triggerFlow = buildTriggerFlowDTO();
        triggerFlow.setId(4L);
        TriggerFlowDTO newFlow = flowManagementService.updateFlow(triggerFlow);
        Assert.assertEquals(newFlow.getName(), triggerFlow.getName());
    }

    @Test
    public void list() {
        List<TriggerFlowDTO> flows = flowManagementService.list();
        Assert.assertNotNull(flows);
    }

    @Test
    public void copy() {
        CopyFlow copyFlow = new CopyFlow();
        copyFlow.setId(4L);
        copyFlow.setName("new flow from 4L");
        Long id = flowManagementService.copyFlow(copyFlow);
        TriggerFlowEntity origin = flowRepository.findOne(4L);
        TriggerFlowEntity copy = flowRepository.findOne(id);
        Assert.assertEquals(origin.getPipeline(), copy.getPipeline());
    }

    @Test
    public void query() {
        FlowQuery query = new FlowQuery();
        query.setModuleId(1L);
        List<Long> flowIds = new ArrayList<>();
        flowIds.add(4L);
        List<TriggerFlowDTO> flows = flowManagementService.queryFlows(query);
        flows.forEach(flow -> {
            Assert.assertSame(flow.getModuleId(), 1L);
        });
    }

    @Test
    public void validate() {
        List<FlowValidation> validations = flowManagementService.validateFlow(buildTriggerFlowDTO());
        Assert.assertNotNull(validations);
    }

    @Test
    public void listBasicFlowInfoByModuleId() {
        List<BasicFlowInfo> basicFlowInfos = flowManagementService.listBasicFlowInfoByModuleId(1L);
        basicFlowInfos.forEach(basicFlowInfo -> {
            Assert.assertSame(basicFlowInfo.getModuleId(), 1L);
        });
    }

    @Test
    public void getById() {
        TriggerFlowDTO flow = flowManagementService.getById(4L);
        TriggerFlowEntity entity = triggerFlowRepository.findOne(4L);
        Assert.assertEquals(flow.getName(), entity.getName());
    }

    @Test
    @Rollback
    public void delete() {
        flowManagementService.deleteFlow(5L);
        Assert.assertTrue("", true);
    }

    private TriggerFlowDTO buildTriggerFlowDTO() {
        TriggerFlowDTO flow = new TriggerFlowDTO();
        flow.setName("dalaran-flow");
        flow.setModuleId(1L);
        flow.setTriggerType("netty-http-listener");
        flow.setDescription("test");
        flow.setOutModelId(23L);
        flow.setInModelId(23L);

        List<ProcessorDTO> processors = new ArrayList<>();
        ProcessorDTO processor1 = new ProcessorDTO();
        processor1.setId("1");
        processor1.setName("dalaran-processor-1");
        processor1.setType("http-client");

        /**
         * {"path":"/test", "method":"GET", "connectorId":"4", "outModelId":"23"}
         *
         * {"protocol":"HTTP", "host":"localhost", "port":"8080"}
         */
        Map<String, Object> processorConfig = JSON.parseObject("{\"path\":\"/test\", \"method\":\"GET\", \"connectorId\":\"4\", \"outModelId\":\"23\"}", Map.class);
        processor1.setConfig(processorConfig);
        processors.add(processor1);
        flow.setPipeline(processors);

        /**
         * {"path": "/news", "itemType": "Start", "inModelId": 14, "protocol": "HTTP", "method": "GET", "outModelId": 15, "type":"netty-http-listener", "timeout":"3000"}
         */
        Map<String, Object> triggerConfig = JSON.parseObject("{\"path\": \"/news\", \"itemType\": \"Start\", \"inModelId\": 23, \"protocol\": \"HTTP\", \"method\": \"GET\", \"outModelId\": 23, \"type\":\"netty-http-listener\", \"timeout\":\"3000\"}");
        flow.setTriggerConfig(triggerConfig);

        return flow;
    }

    private TriggerFlowEntity buildEntity(TriggerFlowDTO model) {
        TriggerFlowEntity flowEntity;
        Long id = model.getId();
        if (id != null) {
            flowEntity = flowRepository.findOne(id);
        } else {
            flowEntity = new TriggerFlowEntity();
        }

        List<ProcessorEntity> pipeline = model.getPipeline().stream().map(processor -> {
            ProcessorEntity processorEntity = new ProcessorEntity();
            processorEntity.setId(processor.getId());
            processorEntity.setType(processor.getType());
            processorEntity.setName(processor.getName());
            processorEntity.setConfig(JSON.toJSONString(processor.getConfig()));
            return processorEntity;
        }).collect(Collectors.toList());

        String name = model.getName();
        if (StringUtils.isNoneBlank(name)) {
            flowEntity.setName(name);
        } else {
            flowEntity.setName("Dalaran Flow");
        }
        flowEntity.setTriggerType(model.getTriggerType());
        flowEntity.setTriggerConfig(JSON.toJSONString(model.getTriggerConfig()));
        flowEntity.setModuleId(model.getModuleId());
        flowEntity.setInModel(model.getInModelId());
        flowEntity.setOutModel(model.getOutModelId());
        flowEntity.setPipeline(pipeline);
        flowEntity.setDescription(model.getDescription());

        return flowEntity;
    }

    private List<FlowValidation> validateFlow(TriggerFlowEntity entity) {
        TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(entity);
        return flowBuilder.validateFlow(triggerFlow);
    }

    private ConnectorDTO buildConnector() {
        ConnectorDTO connector = new ConnectorDTO();
        connector.setModuleId(1L);
        connector.setName("test-connector");
        connector.setComponentType(ComponentType.Processor);
        connector.setComponentName("http-client");

        /**
         * {"host":"localhost","protocol":"HTTP","port":"8080","timeout":"3000"}
         */
        String str = "{\"host\":\"localhost\",\"protocol\":\"HTTP\",\"port\":\"8080\",\"timeout\":\"3000\"}";
        Map<String, Object> config = JSON.parseObject(str, Map.class);
        connector.setConfig(config);

        return connector;
    }
}
