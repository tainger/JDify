package io.terminus.dalaran.console.rest;

import com.alibaba.dubbo.common.utils.IOUtils;
import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.console.model.TestResult;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.entity.ModelEntity;
import io.terminus.dalaran.repository.PropertyRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/flow")
public class FlowManagementRest {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private FlowManagementService flowManagementService;

    @Autowired
    private ModelManagementService modelManagementService;

    @Autowired
    private DalaranContext dalaranContext;

    @ApiOperation(value = "创建工作流")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Long create(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.createFlow(model);
    }

    @ApiOperation(value = "更新工作流")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public TriggerFlowDTO update(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.updateFlow(model);
    }

    @ApiOperation(value = "删除工作流")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(@RequestParam Long id) {
        flowManagementService.deleteFlow(id);
    }

    @ApiOperation(value = "条件查询工作流")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<TriggerFlowDTO> query(FlowQuery query) {
        return flowManagementService.queryFlows(query);
    }

    @ApiOperation(value = "全量查询工作流")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<TriggerFlowDTO> list() {
        return flowManagementService.list();
    }

    @RequestMapping(value = "/queryByProcessorIds", method = RequestMethod.GET)
    public List<TriggerFlowDTO> queryByProcessorIds(@RequestParam List<Long> processorIds) {
        return flowManagementService.queryByProcessorIds(processorIds);
    }

    @PostMapping("/{flowId}/test")
    private TestResult doTest(@PathVariable Long flowId, @RequestBody String body) {
        String recordId = nextRecordId();

        TriggerFlowDTO flow = flowManagementService.getById(flowId);
        if (flow == null) {
            // TODO throw flow not found
            return null;
        }
        ModelEntity inModel = modelManagementService.getById(flow.getOutModelId());

        BodyType bodyType = inModel.getType();

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
            result.setBodyType(BodyType.EXCEPTION);
        }
        return result;
    }

    // TODO 这里可以考虑换一下 camel 的 uuid 生成器
    private String nextRecordId() {
        return RandomStringUtils.randomAlphanumeric(16);
    }
}
