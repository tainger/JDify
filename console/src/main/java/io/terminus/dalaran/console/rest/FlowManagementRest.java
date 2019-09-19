package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.TestRequestDTO;
import io.terminus.dalaran.console.model.dto.CopyFlow;
import io.terminus.dalaran.console.model.dto.ImportFlowResult;
import io.terminus.dalaran.console.model.dto.ImportProcessorDTO;
import io.terminus.dalaran.console.model.dto.ImportProcessorResult;
import io.terminus.dalaran.console.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.core.context.DalaranContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @DalaranException(value = ResponseMessage.FLOW_CREATE_ERROR)
    public Long create(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.createFlow(model);
    }

    @ApiOperation(value = "更新集成流")
    @PostMapping(value = "/update")
    @DalaranException(value = ResponseMessage.FLOW_UPDATE_ERROR)
    public TriggerFlowDTO update(@RequestBody TriggerFlowDTO model) {
        return flowManagementService.updateFlow(model);
    }

    @ApiOperation(value = "快速创建集成流")
    @PostMapping(value = "/import")
    @DalaranException(value = ResponseMessage.FLOW_CREATE_ERROR)
    public ImportFlowResult importTriggerFlow(@RequestBody ImportFlowDTO model) {
        return flowManagementService.importFlow(model);
    }

    @ApiOperation(value = "快速创建处理器")
    @PostMapping(value = "/importProcessor")
    @DalaranException(value = ResponseMessage.FLOW_CREATE_ERROR)
    public ImportProcessorResult importProcessor(@RequestBody ImportProcessorDTO model) {
        return flowManagementService.importProcessor(model);
    }

    @ApiOperation(value = "删除集成流")
    @DeleteMapping(value = "/delete")
    public Response delete(@RequestParam Long id) {
        try {
            flowManagementService.deleteFlow(id);
            return Response.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FLOW_DELETE_ERROR);
        }
    }

    @ApiOperation(value = "复制集成流")
    @PostMapping(value = "/copy")
    public Response copy(@RequestBody CopyFlow copyFlow) {
        try {
            return Response.ok(flowManagementService.copyFlow(copyFlow));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FLOW_COPY_ERROR);
        }
    }

    @ApiOperation(value = "根据 ID 获取集成流")
    @GetMapping(value = "/{id}")
    public Response getById(@PathVariable Long id) {
        try {
            return Response.ok(flowManagementService.getById(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FLOW_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "条件查询集成流")
    @GetMapping(value = "/query")
    public Response query(FlowQuery query) {
        try {
            return Response.ok(flowManagementService.queryFlows(query));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FLOW_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "全量查询集成流")
    @GetMapping(value = "/list")
    public Response list() {
        try {
            return Response.ok(flowManagementService.list());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FLOW_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "检查集成流")
    @PostMapping(value = "/validate")
    public Response validate(@RequestBody TriggerFlowDTO model) {
        try {
            return Response.ok(flowManagementService.validateFlow(model));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FLOW_CHECK_ERROR);
        }
    }

    @ApiOperation(value = "测试集成流")
    @PostMapping("/test")
    private Response doTest(@RequestBody TestRequestDTO request) {
        try {
            TriggerFlowDTO flow = flowManagementService.getById(request.getFlowId());
            if (flow == null) {
                // TODO throw flow not found
                return null;
            }
            String recordId = dalaranContext.testFlow(request.getFlowId(), request.getBody());
            return Response.ok(tracingLogService.getRecordDetail(recordId));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.FLOW_TEST_ERROR);
        }
    }
}
