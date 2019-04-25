package io.terminus.dalaran.console.rest;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.console.model.FlowModel;
import io.terminus.dalaran.console.model.TestResult;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.entity.FlowEntity;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.entity.PropertyEntity;
import io.terminus.dalaran.repository.ProcessorRepository;
import io.terminus.dalaran.repository.PropertyRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flow")
public class FlowManagementRest {

    // TODO for test
    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private FlowManagementService flowManagementService;

    @Autowired
    private DalaranContext dalaranContext;

    private Gson gson = new Gson();

    @PutMapping
    public Long saveFlow(@RequestBody FlowModel flowModel) {
        FlowEntity flowEntity = new FlowEntity();
//        TriggerEntity triggerEntity = new TriggerEntity();

        List<Long> processorEntitySet = flowModel.getProcessors().stream().map(processorModel -> {
            ProcessorEntity processorEntity = new ProcessorEntity();
            processorEntity.setType(processorModel.getType());
            processorEntity.setConfig(gson.toJson(processorModel.getConfig()));
            processorRepository.save(processorEntity);
            return processorEntity.getId();
        }).collect(Collectors.toList());


        if (flowModel.getProperties() != null) {
            List<Long> propertyEntitySet = flowModel.getProperties().entrySet().stream().map(entry -> {
                PropertyEntity propertyEntity = new PropertyEntity();
                propertyEntity.setName(entry.getKey());
                propertyEntity.setValue(entry.getValue());
                propertyRepository.save(propertyEntity);
                return propertyEntity.getId();
            }).collect(Collectors.toList());
            flowEntity.setProperties(propertyEntitySet);
        }

        flowEntity.setProcessors(processorEntitySet);

        flowEntity.setId(flowModel.getId());
        flowEntity.setName(flowModel.getName());
        flowEntity.setDescription(flowModel.getDescription());

//        triggerEntity.setType(flowModel.getTrigger().getType());
//        triggerEntity.setConfig(gson.toJson(flowModel.getTrigger().getConfig()));

        return flowManagementService.saveFlow(flowEntity);
    }

//    @ApiOperation(value="流程发布", notes="后边应该会改动，目前的发布流程是trigger跟processors一起的")
//    @PostMapping("/publish")
//    void publish() {
//        flowManagementService.publish();
//    }
//
//    @ApiOperation(value = "模型保存并发布")
//    @PostMapping("/saveAndPublish")
//    void publish(@RequestBody FlowModel flowModel) {
//        saveFlow(flowModel);
//        flowManagementService.publish();
//    }

    @ApiOperation(value = "创建工作流")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Long create(@RequestBody FlowModel model) {
        return flowManagementService.createFlow(model);
    }

    @ApiOperation(value = "更新工作流")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public FlowModel update(@RequestBody FlowModel model) {
        return flowManagementService.updateFlow(model);
    }

    @ApiOperation(value = "删除工作流")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(@RequestParam Long id) {
        flowManagementService.deleteFlow(id);
    }

    @ApiOperation(value = "条件查询工作流")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<FlowModel> query(FlowQuery query) {
        return flowManagementService.queryFlows(query);
    }

    @ApiOperation(value = "全量查询工作流")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<FlowModel> list() {
        return flowManagementService.list();
    }

    @RequestMapping(value = "/queryByProcessorIds", method = RequestMethod.GET)
    public List<FlowModel> queryByProcessorIds(@RequestParam List<Long> processorIds) {
        return flowManagementService.queryByProcessorIds(processorIds);
    }

    @PostMapping("/{flowId}/test")
    private TestResult doTest(@PathVariable Long flowId, @RequestBody String body) throws IOException {
        String recordId = nextRecordId();

        FlowModel flow = flowManagementService.getById(flowId);
        if (flow == null) {
            // TODO throw flow not found
            return null;
        }
        BodyModelType bodyType = flow.getOutStructure().getStructureType();

        TestResult result = new TestResult();
        result.setBodyType(bodyType);
        result.setLogRecordId(recordId);
        try {
            Object data = dalaranContext.testFlow(flowId, body, recordId);
            // TODO on error
            result.setSuccessful(true);
            // TODO 如果最后一步是序列化的情况, 会被序列化成 byte[], 先 toString 一下
            if (data instanceof String) {
                result.setBody((String) data);
            }
            if (data instanceof byte[]) {
                result.setBody(new String((byte[]) data));
            }
            // TODO 这里的输出还是要考虑抽象一下....
            if (data instanceof InputStream) {
                InputStream input = (InputStream) data;
                String resultBody = StringUtils.join(IOUtils.readLines(input), "\n");
                result.setBody(resultBody);
            }
        } catch (Throwable e) {
            result.setSuccessful(false);
            result.setBody(e.getMessage());
            result.setBodyType(BodyModelType.EXCEPTION);
        }
        return result;
    }

    // TODO 这里可以考虑换一下 camel 的 uuid 生成器
    private String nextRecordId() {
        return RandomStringUtils.randomAlphanumeric(16);
    }
}
