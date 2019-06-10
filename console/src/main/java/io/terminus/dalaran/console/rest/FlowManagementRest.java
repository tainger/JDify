package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.TestRequestDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.dto.log.MainLogDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.model.FlowValidation;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    private TracingLogService tracingLogService;

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

    @ApiOperation(value = "复制工作流")
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public Long copy(@RequestParam Long id) {
        return flowManagementService.copyFlow(id);
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

    @ApiOperation(value = "检查集成流")
    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public List<FlowValidation> validate(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.validateFlow(model);
    }

    @PostMapping("/test")
    private MainLogDTO doTest(@RequestBody TestRequestDTO request) {
        String recordId = nextRecordId();
        TriggerFlowDTO flow = flowManagementService.getById(request.getFlowId());
        if (flow == null) {
            // TODO throw flow not found
            return null;
        }
        try {
            dalaranContext.testFlow(request.getFlowId(), request.getBody(), recordId);
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
        return tracingLogService.getRecordDetail(recordId);
    }

    // TODO 这里可以考虑换一下 camel 的 uuid 生成器
    private String nextRecordId() {
        return RandomStringUtils.randomAlphanumeric(32);
    }
}
