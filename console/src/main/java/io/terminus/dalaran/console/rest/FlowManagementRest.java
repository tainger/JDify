package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.TestRequestDTO;
import io.terminus.dalaran.console.model.dto.CopyFlow;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.dto.log.MainLogDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.model.flow.FlowValidation;
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

    @ApiOperation(value = "创建集成流")
    @PostMapping(value = "/create")
    public Long create(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.createFlow(model);
    }

    @ApiOperation(value = "更新集成流")
    @PostMapping(value = "/update")
    public TriggerFlowDTO update(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.updateFlow(model);
    }

    @ApiOperation(value = "删除集成流")
    @DeleteMapping(value = "/delete")
    public void delete(@RequestParam Long id) {
        flowManagementService.deleteFlow(id);
    }

    @ApiOperation(value = "复制集成流")
    @PostMapping(value = "/copy")
    public Long copy(@RequestBody CopyFlow copyFlow) {
        return flowManagementService.copyFlow(copyFlow);
    }

    @ApiOperation(value = "根据 ID 获取集成流")
    @GetMapping(value = "/{id}")
    public TriggerFlowDTO getById(@PathVariable Long id) {
        return flowManagementService.getById(id);
    }

    @ApiOperation(value = "条件查询集成流")
    @GetMapping(value = "/query")
    public List<TriggerFlowDTO> query(FlowQuery query) {
        return flowManagementService.queryFlows(query);
    }

    @ApiOperation(value = "全量查询集成流")
    @GetMapping(value = "/list")
    public List<TriggerFlowDTO> list() {
        return flowManagementService.list();
    }

    @ApiOperation(value = "检查集成流")
    @PostMapping(value = "/validate")
    public List<FlowValidation> validate(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.validateFlow(model);
    }

    @ApiOperation(value = "测试集成流")
    @PostMapping("/test")
    private MainLogDTO doTest(@RequestBody TestRequestDTO request) {
        TriggerFlowDTO flow = flowManagementService.getById(request.getFlowId());
        if (flow == null) {
            // TODO throw flow not found
            return null;
        }
        String recordId = dalaranContext.testFlow(request.getFlowId(), request.getBody());
        return tracingLogService.getRecordDetail(recordId);
    }
}
