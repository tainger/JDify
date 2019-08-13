package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.TestRequestDTO;
import io.terminus.dalaran.console.model.dto.CopyFlow;
import io.terminus.dalaran.console.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.console.model.dto.log.MainLogDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.service.SubFlowManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.model.flow.FlowValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sub-flow")
public class SubFlowManagementRest {

    @Autowired
    private SubFlowManagementService service;

    @Autowired
    private TracingLogService tracingLogService;

    @Autowired
    private DalaranContext dalaranContext;

    @ApiOperation(value = "创建子流程")
    @PostMapping(value = "/create")
    public Long create(@RequestBody SubFlowDTO model) {
        return service.createFlow(model);
    }

    @ApiOperation(value = "更新子流程")
    @PostMapping(value = "/update")
    public SubFlowDTO update(@RequestBody SubFlowDTO model) {
        return service.updateFlow(model);
    }

    @ApiOperation(value = "删除子流程")
    @DeleteMapping(value = "/delete")
    public void delete(@RequestParam Long id) {
        service.deleteFlow(id);
    }

    @ApiOperation(value = "复制子流程")
    @PostMapping(value = "/copy")
    public Long copy(@RequestBody CopyFlow copyFlow) {
        return service.copyFlow(copyFlow);
    }

    @ApiOperation(value = "根据 ID 获取子流程")
    @GetMapping(value = "/{id}")
    public SubFlowDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @ApiOperation(value = "条件查询子流程")
    @GetMapping(value = "/query")
    public List<SubFlowDTO> query(FlowQuery query) {
        return service.queryFlows(query);
    }

    @ApiOperation(value = "全量查询子流程")
    @GetMapping(value = "/list")
    public List<SubFlowDTO> list() {
        return service.list();
    }

    @ApiOperation(value = "检查子流程")
    @PostMapping(value = "/validate")
    public List<FlowValidation> validate(@RequestBody SubFlowDTO model) {
        return service.validateFlow(model);
    }

    @ApiOperation(value = "测试子流程")
    @PostMapping("/test")
    private MainLogDTO doSubFlowTest(@RequestBody TestRequestDTO request) {
        SubFlowDTO flow = service.getById(request.getFlowId());
        if (flow == null) {
            // TODO throw flow not found
            return null;
        }
        String recordId = dalaranContext.testSubFlow(request.getFlowId(), request.getBody());
        return tracingLogService.getRecordDetail(recordId);
    }
}
