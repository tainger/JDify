package io.terminus.dalaran.console.flow;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.model.dto.CopyFlow;
import io.terminus.dalaran.console.model.dto.ProcessorDTO;
import io.terminus.dalaran.console.model.dto.flow.BasicFlowInfo;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.TriggerFlowRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.model.FlowValidation;
import io.terminus.dalaran.core.flow.model.TriggerFlow;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
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
    private TriggerFlowRepository flowRepository;

    @Autowired
    private DalaranFlowBuilder flowBuilder;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Test
    public void create() {
        Long id = flowManagementService.createFlow(buildTriggerFlowDTO());
        Assert.assertNotNull(id);
    }

    @Test
    public void save() {
        TriggerFlowEntity entity = buildEntity(buildTriggerFlowDTO());
        Long id = flowManagementService.saveFlow(entity);
        Assert.assertNotNull(id);
    }

    @Test
    public void update() {
        TriggerFlowDTO triggerFlow = buildTriggerFlowDTO();
        triggerFlow.setId(14L);
        TriggerFlowDTO newFlow = flowManagementService.updateFlow(triggerFlow);
        Assert.assertNotNull(newFlow);
    }

    @Test
    public void list() {
        List<TriggerFlowDTO> flows = flowManagementService.list();
        Assert.assertNotNull(flows);
    }

    @Test
    public void copy() {
        CopyFlow copyFlow = new CopyFlow();
        copyFlow.setId(14L);
        copyFlow.setName("new flow from 14");
        Long id = flowManagementService.copyFlow(copyFlow);
        Assert.assertNotNull(id);
    }

    @Test
    public void query() {
        FlowQuery query = new FlowQuery();
        query.setModuleId(1L);
        List<Long> flowIds = new ArrayList<>();
        flowIds.add(14L);
        List<TriggerFlowDTO> flows = flowManagementService.queryFlows(query);
        Assert.assertNotNull(flows);
    }

    @Test
    public void validate() {
        List<FlowValidation> validations = flowManagementService.validateFlow(buildTriggerFlowDTO());
        Assert.assertNotNull(validations);
    }

    @Test
    public void listBasicFlowInfoByModuleId() {
        List<BasicFlowInfo> basicFlowInfos = flowManagementService.listBasicFlowInfoByModuleId(1L);
        Assert.assertNotNull(basicFlowInfos);
    }

    @Test
    public void getById() {
        TriggerFlowDTO flow = flowManagementService.getById(14L);
        Assert.assertNotNull(flow);
    }

    @Test
    @Rollback
    public void delete() {
        flowManagementService.deleteFlow(12L);
        Assert.assertTrue("", true);
    }

    private TriggerFlowDTO buildTriggerFlowDTO() {
        TriggerFlowDTO flow = new TriggerFlowDTO();
        flow.setName("dalaran-flow");
        flow.setModuleId(1L);
        flow.setTriggerType("netty-http-listener");
        flow.setDescription("test");
        flow.setOutModelId(6L);
        flow.setInModelId(6L);

        List<ProcessorDTO> processors = new ArrayList<>();
        ProcessorDTO processor1 = new ProcessorDTO();
        processor1.setId("1");
        processor1.setName("dalaran-processor-1");
        processor1.setType("http-client");

        /**
         * {"path":"/test", "method":"GET", "connectorId":"4", "outModelId":"6"}
         *
         * {"protocol":"HTTP", "host":"localhost", "port":"8080"}
         */
        Map<String, Object> processorConfig = JSON.parseObject("{\"path\":\"/test\", \"method\":\"GET\", \"connectorId\":\"4\", \"outModelId\":\"6\"}", Map.class);
        processor1.setConfig(processorConfig);
        processors.add(processor1);
        flow.setPipeline(processors);

        /**
         * {"path": "/news", "itemType": "Start", "inModelId": 14, "protocol": "HTTP", "method": "GET", "outModelId": 15, "type":"netty-http-listener", "timeout":"3000"}
         */
        Map<String, Object> triggerConfig = JSON.parseObject("{\"path\": \"/news\", \"itemType\": \"Start\", \"inModelId\": 14, \"protocol\": \"HTTP\", \"method\": \"GET\", \"outModelId\": 15, \"type\":\"netty-http-listener\", \"timeout\":\"3000\"}");
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
}
